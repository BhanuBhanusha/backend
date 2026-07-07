package com.example.ZenDesk.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ZenDesk.dto.AgentProfileResponseDto;
import com.example.ZenDesk.entity.AgentProfile;
import com.example.ZenDesk.exception.ResourceNotFoundException;
import com.example.ZenDesk.repository.AgentProfileRepository;
import com.example.ZenDesk.repository.AppUserRepository;
import com.example.ZenDesk.entity.AppUser;

@Service
public class AgentProfileService {
    
    @Autowired
    AgentProfileRepository agentRepo;

    @Autowired
    AppUserRepository userRepo;

    @Autowired
    CurrentUserService currentUserService;

    private AgentProfileResponseDto mapAgentToDto(AgentProfile agent)
    {
        AgentProfileResponseDto response=new AgentProfileResponseDto();
        response.setAgentId(agent.getId());
        response.setDepartment(agent.getDepartment());
        response.setSpecialization(agent.getSpecialization());
        response.setCurrentLoad(agent.getCurrentLoad());
        response.setMaxCapacity(agent.getMaxCapacity());
        response.setAvailable(agent.isAvailable());
        response.setRole(agent.getRole());

        userRepo.findById(agent.getUserId()).ifPresent(user -> {
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
        });

        return response;

    }
    public AgentProfile add(AgentProfile agent)
    {
        return agentRepo.save(agent);
    }
    public List<AgentProfileResponseDto> getAllAgents()
    {
        List<AgentProfile> agents=agentRepo.findAll();
        List<AgentProfileResponseDto> response=new ArrayList<>();

        for(AgentProfile agent:agents)
        {
            response.add(mapAgentToDto(agent));
        }
        return response;
    }
    public AgentProfileResponseDto getAgentById(Long id)
    {
        AgentProfile agent=agentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        return mapAgentToDto(agent);
    }
    public AgentProfileResponseDto updateAgentprofile(Long id,int maxCapacity,boolean isAvailable,String department,String specialization)
    {
        AgentProfile agent = agentRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Agent not found"));
        agent.setMaxCapacity(maxCapacity);
        agent.setAvailable(isAvailable);
        agent.setDepartment(department);
        agent.setSpecialization(specialization);
        agentRepo.save(agent);

        return mapAgentToDto(agent);
    }
    public AgentProfileResponseDto toggleAvailability(Long id)
    {
        AgentProfile agent=agentRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Agent Not Found"));
        agent.setAvailable(!agent.isAvailable());
        agentRepo.save(agent);
        return mapAgentToDto(agent);
    }
    public List<AgentProfileResponseDto> getWorkloadSummary()
    {
        List<AgentProfile> agents=agentRepo.findAll();
        List<AgentProfileResponseDto> list=new ArrayList<>();
        for(AgentProfile agent:agents)
        {
            AgentProfileResponseDto dto=mapAgentToDto(agent);
            
            double load=0;
            if(agent.getMaxCapacity() > 0)
            { 
            load=((double) agent.getCurrentLoad() / agent.getMaxCapacity()) * 100;
            }
            dto.setLoadPercentage(Math.round(load * 10.0) / 10.0);
            list.add(dto);
        }
        list.sort((a,b)-> Double.compare(b.getLoadPercentage(),a.getLoadPercentage()) );
        return list;
    }

    public AgentProfileResponseDto getProfileByCurrentUser() {
        AppUser currentUser = currentUserService.getCurrentUser();
        AgentProfile agent = agentRepo.findByUserId(currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Agent profile not found for user: " + currentUser.getId()));
        return mapAgentToDto(agent);
    }
}
