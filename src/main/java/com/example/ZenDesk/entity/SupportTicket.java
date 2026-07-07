package com.example.ZenDesk.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name="support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    
    @Column(nullable=false)
    private String title;

    @Column(columnDefinition="TEXT",nullable=false)
    private String description;
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Priority priority=Priority.MEDIUM;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime slaDeadline;

    @PrePersist
    public void onCreate()
    {
        createdAt=LocalDateTime.now();
        status=Status.OPEN;
        calculateSlaDeadline();
    }

    @PreUpdate
    public void onUpdate()
    {
        updatedAt=LocalDateTime.now();
    }

    public void calculateSlaDeadline()
    {
        switch(priority)
        {
            case LOW -> slaDeadline=createdAt.plusHours(72);
            case MEDIUM -> slaDeadline=createdAt.plusHours(24);
            case HIGH -> slaDeadline=createdAt.plusHours(12);
            case CRITICAL -> slaDeadline=createdAt.plusHours(4);
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getSlaDeadline() {
        return slaDeadline;
    }

    public void setSlaDeadline(LocalDateTime slaDeadline) {
        this.slaDeadline = slaDeadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
}
