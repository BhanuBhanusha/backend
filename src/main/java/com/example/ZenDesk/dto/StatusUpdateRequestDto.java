package com.example.ZenDesk.dto;

import com.example.ZenDesk.entity.Status;

import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequestDto {
    
    @NotNull(message="status cannot be null")
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
