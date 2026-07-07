package com.example.ZenDesk.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.ZenDesk.dto.StatusUpdateRequestDto;
import com.example.ZenDesk.dto.TicketRequestDto;
import com.example.ZenDesk.dto.TicketResponseDto;
import com.example.ZenDesk.entity.AgentProfile;
import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.entity.Status;
import com.example.ZenDesk.entity.SupportTicket;
import com.example.ZenDesk.entity.TicketAssignment;
import com.example.ZenDesk.exception.BusinessValidationException;
import com.example.ZenDesk.exception.ResourceNotFoundException;
import com.example.ZenDesk.repository.AgentProfileRepository;
import com.example.ZenDesk.repository.AppUserRepository;
import com.example.ZenDesk.repository.SupportTicketRepository;
import com.example.ZenDesk.repository.TicketAssignmentRepository;
import com.example.ZenDesk.repository.TicketCommentRepository;

import jakarta.transaction.Transactional;

@Service
public class TicketService {

    private final SupportTicketRepository ticketRepo;
    private final AgentProfileRepository agentRepo;
    private final TicketAssignmentRepository assignmentRepo;
    private final AppUserRepository userRepo;
    private final TicketCommentRepository commentRepo;
    private final CurrentUserService currentUserService;

    public TicketService(AgentProfileRepository agentRepo, SupportTicketRepository ticketRepo, TicketAssignmentRepository assignmentRepo, AppUserRepository userRepo, TicketCommentRepository commentRepo, CurrentUserService currentUserService) {
        this.agentRepo = agentRepo;
        this.ticketRepo = ticketRepo;
        this.assignmentRepo=assignmentRepo;
        this.userRepo = userRepo;
        this.commentRepo=commentRepo;
        this.currentUserService=currentUserService;
    }

    private TicketResponseDto mapToDto(SupportTicket ticket)
    {
        TicketResponseDto response=new TicketResponseDto();
        response.setTicketId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setCreatorId(ticket.getCreatorId());
        response.setDescription(ticket.getDescription());
        response.setCategory(ticket.getCategory());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setSlaDeadline(ticket.getSlaDeadline());

        userRepo.findById(ticket.getCreatorId()).ifPresent(creator -> {
            response.setCreatorName(creator.getFullName());
        });

        response.setCommentCount((int) commentRepo.countByTicketId(ticket.getId()));

        assignmentRepo.findByTicketIdAndIsActiveTrue(ticket.getId()).ifPresent(assignment -> {
            agentRepo.findById(assignment.getAgentId()).ifPresent(agent -> {
                userRepo.findById(agent.getUserId()).ifPresent(user -> {
                    response.setAssignedAgentName(user.getFullName());
                });
            });
        });

        return response;
    }
    public TicketResponseDto createTicket(TicketRequestDto dto)
    {
        AppUser creator=currentUserService.getCurrentUser();
        SupportTicket ticket=new SupportTicket();
        ticket.setTitle(dto.getTitle());
        ticket.setCreatorId(creator.getId());
        ticket.setDescription(dto.getDescription());
        ticket.setCategory(dto.getCategory());
        ticket.setPriority(dto.getPriority());

        SupportTicket saved = ticketRepo.save(ticket);
        return mapToDto(saved);
    }
    private static final Map<Status,Set<Status>> VALID_TRANSITIONS=Map.of(
        Status.OPEN,Set.of(Status.IN_PROGRESS,Status.CLOSED,Status.ESCALATED),
        Status.IN_PROGRESS,Set.of(Status.RESOLVED,Status.ESCALATED,Status.OPEN),
        Status.ESCALATED,Set.of(Status.IN_PROGRESS,Status.CLOSED),
        Status.RESOLVED,Set.of(Status.CLOSED,Status.OPEN),
        Status.CLOSED,Set.of());
    
    @Transactional
    public TicketResponseDto updateStatus(Long ticketId,StatusUpdateRequestDto dto)
    {
        SupportTicket ticket=ticketRepo.findById(ticketId).orElseThrow(()-> new ResourceNotFoundException("Ticket is unavailable"));
        Status currentStatus=ticket.getStatus();
        Status newStatus=dto.getStatus();
        if(currentStatus==newStatus)
        {
            throw new BusinessValidationException("Ticket already in status "+newStatus);
        }
        if(!VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(newStatus))
        {
            throw new BusinessValidationException("Invalid transition");
        }
        ticket.setStatus(newStatus);
        if(newStatus == Status.RESOLVED)
        {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        if(currentStatus == Status.RESOLVED && newStatus == Status.OPEN)
        {
            ticket.setResolvedAt(null);
        }
        SupportTicket saved = ticketRepo.save(ticket);
        return mapToDto(saved);
    }

    public TicketResponseDto getTicketById(Long id)
    {
        SupportTicket ticket=ticketRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Ticket unavailable"));
        return mapToDto(ticket);
    }

    public List<TicketResponseDto> getAllTickets()
    {
        List<SupportTicket> tickets=ticketRepo.findAll();
        List<TicketResponseDto> list=new ArrayList<>();
        for (SupportTicket ticket : tickets) 
        {
            list.add(mapToDto(ticket));
        }
        return list;
    }

    public List<TicketResponseDto> getTicketsByAgent(Long userId)
    {
        AgentProfile agent = agentRepo.findByUserId(userId).orElseThrow(()-> new ResourceNotFoundException("Agent not found"));
        List<TicketAssignment> assignments = assignmentRepo.findByAgentIdAndIsActiveTrue(agent.getId()) ;
        List<TicketResponseDto> response = new ArrayList<>();
        for (TicketAssignment assignment : assignments) 
        {
            SupportTicket ticket = ticketRepo.findById(assignment.getTicketId()).orElseThrow(()-> new ResourceNotFoundException("Ticket Not Found"));
            response.add(mapToDto(ticket));
        }
        return response;

    }

    public List<TicketResponseDto> getTicketsByCreator(Long creatorId)
    {
        List<SupportTicket> tickets = ticketRepo.findByCreatorId(creatorId);
        List<TicketResponseDto> response = new ArrayList<>();
        for (SupportTicket ticket : tickets) 
        {
            response.add(mapToDto(ticket));     
        }
        return response;
    }

    public List<TicketResponseDto> getTicketsByStatus(Status status)
    {
        List<SupportTicket> tickets = ticketRepo.findByStatus(status);
        List<TicketResponseDto> response = new ArrayList<>();
        for (SupportTicket ticket : tickets) 
        {
            response.add(mapToDto(ticket));     
        }
        return response;
    }

    public List<TicketResponseDto> getTicketsAtSlaRisk()
    {
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime deadline=now.plusHours(24);

        List<SupportTicket> tickets = ticketRepo.findTicketsAtSlaRisk(now, deadline);
        List<TicketResponseDto> response = new ArrayList<>();
        for (SupportTicket ticket : tickets) 
        {
            response.add(mapToDto(ticket));     
        }
        return response;
    }

    public Map<String,Long> getTicketStats()
    {
        Map<String,Long> map=new HashMap<>();
        map.put("OPEN",ticketRepo.countByStatus(Status.OPEN));
        map.put("IN_PROGRESS",ticketRepo.countByStatus(Status.IN_PROGRESS));
        map.put("RESOLVED",ticketRepo.countByStatus(Status.RESOLVED));
        map.put("CLOSED",ticketRepo.countByStatus(Status.CLOSED));
        map.put("ESCALATED",ticketRepo.countByStatus(Status.ESCALATED));
        return map;

    }

}
