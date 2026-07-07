package com.example.ZenDesk.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ZenDesk.dto.AssignmentRequestDto;
import com.example.ZenDesk.service.AssignmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    
    @Autowired
    AssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String,String>> assignTicket(@Valid @RequestBody AssignmentRequestDto dto)
    {
        assignmentService.assignTicket(dto);
        return ResponseEntity.ok(Map.of("message","Ticket assigned successfully"));
    }

    @PostMapping("/auto-assign/{ticketId}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String,String>> autoAssignTicket(@PathVariable Long ticketId)
    {
        String message=assignmentService.autoAssignTicket(ticketId);
        return ResponseEntity.ok(Map.of("message",message));
    }

    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String,String>> unassignTicket(@PathVariable Long ticketId)
    {
        assignmentService.unassignTicket(ticketId);
        return ResponseEntity.ok(Map.of("message","Ticket unassigned successfully"));
    }
}
