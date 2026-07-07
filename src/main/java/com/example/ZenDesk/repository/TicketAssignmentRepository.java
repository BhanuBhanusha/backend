package com.example.ZenDesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ZenDesk.entity.TicketAssignment;

@Repository
public interface TicketAssignmentRepository extends JpaRepository<TicketAssignment, Long>{
    Optional<TicketAssignment> findByTicketIdAndIsActiveTrue(Long ticketId);
    List<TicketAssignment> findByAgentIdAndIsActiveTrue(Long agentId);
    Long countByAgentIdAndIsActiveTrue(Long agentId);
}
