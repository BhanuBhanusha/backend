package com.example.ZenDesk.dto;

import jakarta.validation.constraints.NotNull;

public class AssignmentRequestDto {
    @NotNull(message="Ticket id cannot be null")
    private Long ticketId;

    @NotNull(message="Agent id cannot be null")
    private Long agentId;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

}
