package com.example.ZenDesk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ZenDesk.dto.AgentProfileResponseDto;
import com.example.ZenDesk.entity.AgentProfile;
import com.example.ZenDesk.service.AgentProfileService;

@RestController
@RequestMapping("/agents")
public class AgentController {
    
    @Autowired
    AgentProfileService agentService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<AgentProfile> add(@RequestBody AgentProfile agent)
    {
        return ResponseEntity.status(201).body(agentService.add(agent));
    }

    @GetMapping()
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<AgentProfileResponseDto>> getAll()
    {
        return ResponseEntity.status(200).body(agentService.getAllAgents());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT','SUPERVISOR')")
    public ResponseEntity<AgentProfileResponseDto> getAgent(@PathVariable Long id)
    {
        return ResponseEntity.status(200).body(agentService.getAgentById(id));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<AgentProfileResponseDto> updateAgentprofile(@RequestParam Long id,@RequestParam int maxCapacity,@RequestParam boolean isAvailable,@RequestParam String department,@RequestParam String specialization)
    {
        return ResponseEntity.ok(agentService.updateAgentprofile(id, maxCapacity, isAvailable, department, specialization));
    }

    @PutMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<AgentProfileResponseDto> toggleAvailability(@PathVariable Long id)
    {
        return ResponseEntity.ok(agentService.toggleAvailability(id));
    }
    
    @GetMapping("/workload")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<AgentProfileResponseDto>> getWorkloadSummary()
    {
        return ResponseEntity.ok(agentService.getWorkloadSummary());
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT','SUPERVISOR')")
    public ResponseEntity<AgentProfileResponseDto> getMyProfile()
    {
        return ResponseEntity.ok(agentService.getProfileByCurrentUser());
    }
}
