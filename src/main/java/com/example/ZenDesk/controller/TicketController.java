package com.example.ZenDesk.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ZenDesk.dto.StatusUpdateRequestDto;
import com.example.ZenDesk.dto.TicketRequestDto;
import com.example.ZenDesk.dto.TicketResponseDto;
import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.entity.Role;
import com.example.ZenDesk.entity.Status;
import com.example.ZenDesk.service.CurrentUserService;
import com.example.ZenDesk.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CurrentUserService currentUserService;
    
    public TicketController(TicketService ticketService,CurrentUserService currentUserService) 
    {
        this.ticketService=ticketService;
        this.currentUserService=currentUserService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TicketResponseDto>  createTicket(@Valid @RequestBody TicketRequestDto dto)
    {
        return ResponseEntity.status(201).body(ticketService.createTicket(dto));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getTickets()
    {
        AppUser user = currentUserService.getCurrentUser();
        if(user.getRole() == Role.CUSTOMER)
        {
            return ResponseEntity.ok(ticketService.getTicketsByCreator(user.getId()));
        }
        if(user.getRole() == Role.SUPPORT_AGENT)
        {
            return ResponseEntity.ok(ticketService.getTicketsByAgent(user.getId()));
        }
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable Long id)
    {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT', 'SUPERVISOR')")
    public ResponseEntity<TicketResponseDto> updateStatus(@PathVariable Long id,@Valid @RequestBody StatusUpdateRequestDto dto)
    {
        return ResponseEntity.ok(ticketService.updateStatus(id, dto));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT', 'SUPERVISOR')")
    public ResponseEntity<List<TicketResponseDto>> getTicketByStatus(@PathVariable Status status)
    {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }

    @GetMapping("/sla-risk")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT', 'SUPERVISOR')")
    public ResponseEntity<List<TicketResponseDto>> getTicketBySlaRisk()
    {
        return ResponseEntity.ok(ticketService.getTicketsAtSlaRisk());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT', 'SUPERVISOR')")
    public ResponseEntity<Map<String,Long>> getTicketStats()
    {
        return ResponseEntity.ok(ticketService.getTicketStats());
    }

}
