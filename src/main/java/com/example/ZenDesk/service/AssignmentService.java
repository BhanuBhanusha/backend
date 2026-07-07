package com.example.ZenDesk.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ZenDesk.dto.AssignmentRequestDto;
import com.example.ZenDesk.entity.AgentProfile;
import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.entity.Status;
import com.example.ZenDesk.entity.SupportTicket;
import com.example.ZenDesk.entity.TicketAssignment;
import com.example.ZenDesk.exception.BusinessValidationException;
import com.example.ZenDesk.exception.ResourceNotFoundException;
import com.example.ZenDesk.repository.AgentProfileRepository;
import com.example.ZenDesk.repository.SupportTicketRepository;
import com.example.ZenDesk.repository.TicketAssignmentRepository;

@Service
public class AssignmentService {
    
    private final SupportTicketRepository ticketRepo;
    private final TicketAssignmentRepository assignmentRepo;
    private final AgentProfileRepository agentRepo;
    private final CurrentUserService currentUserService;

    public AssignmentService(AgentProfileRepository agentRepo, TicketAssignmentRepository assignmentRepo, CurrentUserService currentUserService, SupportTicketRepository ticketRepo) {
        this.agentRepo = agentRepo;
        this.assignmentRepo = assignmentRepo;
        this.currentUserService = currentUserService;
        this.ticketRepo = ticketRepo;
    }

    
    @Transactional
    public void assignTicket(AssignmentRequestDto dto)
    {
        AppUser assignedBy = currentUserService.getCurrentUser();
        SupportTicket ticket = ticketRepo.findById(dto.getTicketId()).orElseThrow(()-> new ResourceNotFoundException("Ticket not found"));
        if(ticket.getStatus() == Status.CLOSED || ticket.getStatus() == Status.RESOLVED)
        {
            throw new BusinessValidationException("Cannot assign a closed or resolved ticket");
        }
        AgentProfile agent = agentRepo.findById(dto.getAgentId()).orElseThrow(()-> new ResourceNotFoundException("Agent not found"));
        if(!agent.isAvailable())
        {
            throw new BusinessValidationException("Selected Agent is not available");
        }
        if(agent.getCurrentLoad() >= agent.getMaxCapacity())
        {
            throw new BusinessValidationException("Agent has reached maximum capacity");
        }

        //deactivate assignment 
        assignmentRepo.findByTicketIdAndIsActiveTrue(ticket.getId()).ifPresent(existing -> { 
            existing.setIsActive(false);
            assignmentRepo.save(existing);
            AgentProfile oldAgent = agentRepo.findById(existing.getAgentId()).orElseThrow(()-> new ResourceNotFoundException("Agent not found"));
            oldAgent.setCurrentLoad(Math.max(0,oldAgent.getCurrentLoad()-1));
            agentRepo.save(oldAgent);
        });

        //create new assignment
        TicketAssignment assignment=new TicketAssignment();
        assignment.setTicketId(dto.getTicketId());
        assignment.setAgentId(dto.getAgentId());
        assignment.setAssignedById(assignedBy.getId());
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setIsActive(true);
        assignmentRepo.save(assignment);

        agent.setCurrentLoad(agent.getCurrentLoad()+1);
        agentRepo.save(agent);

        if(ticket.getStatus() == Status.OPEN || ticket.getStatus() == Status.ESCALATED)
        {
            ticket.setStatus(Status.IN_PROGRESS);
            ticketRepo.save(ticket);
        }
    }

    public String autoAssignTicket(Long ticketId)
    {
        List<AgentProfile> agents=agentRepo.findLeastLoadedAgent();
        if(agents.isEmpty())
        {
            throw new BusinessValidationException("No agent is available");
        }
        AgentProfile agent=agents.get(0);
        AssignmentRequestDto dto=new AssignmentRequestDto();
        dto.setTicketId(ticketId);
        dto.setAgentId(agent.getId());
        assignTicket(dto);
        
        return "Ticket assigned successfully";
    }
    
    @Transactional
    public void unassignTicket(Long ticketId)
    {
        TicketAssignment assignment=assignmentRepo.findByTicketIdAndIsActiveTrue(ticketId).orElseThrow(()-> new ResourceNotFoundException("Ticket is not available"));
        assignment.setIsActive(false);
        assignmentRepo.save(assignment);
        AgentProfile agent=agentRepo.findById(assignment.getAgentId()).orElseThrow(()-> new ResourceNotFoundException("Agent not found"));
        agent.setCurrentLoad(Math.max(0,agent.getCurrentLoad()-1));
        agentRepo.save(agent);
        SupportTicket ticket=ticketRepo.findById(ticketId).orElseThrow(()-> new ResourceNotFoundException("Ticket not found"));
        ticket.setStatus(Status.OPEN);
        ticketRepo.save(ticket);
    }
}
