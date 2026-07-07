package com.example.ZenDesk.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ZenDesk.entity.Priority;
import com.example.ZenDesk.entity.Status;
import com.example.ZenDesk.entity.SupportTicket;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long>{

    List<SupportTicket> findByStatus(Status status);
    List<SupportTicket> findByCreatorId(Long creatorId);
    List<SupportTicket> findByPriority(Priority priority);

    @Query("SELECT t FROM SupportTicket t WHERE t.slaDeadline BETWEEN :now AND :deadline AND t.status NOT IN(com.example.ZenDesk.entity.Status.RESOLVED,com.example.ZenDesk.entity.Status.CLOSED)")
    List<SupportTicket> findTicketsAtSlaRisk(@Param("now") LocalDateTime now,@Param("deadline")LocalDateTime deadline);

    Long countByStatus(Status status);
    Long countByCreatorId(Long creatorId);

    @Query("SELECT t FROM SupportTicket t WHERE t.category = :category AND t.status <> com.example.ZenDesk.entity.Status.CLOSED")
    List<SupportTicket> findOpenByCategory(@Param("category")String category);
}
