package com.example.ZenDesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ZenDesk.entity.AgentProfile;

@Repository
public interface AgentProfileRepository extends JpaRepository<AgentProfile,Long>{

    Optional<AgentProfile> findByUserId(Long userId);
    @Query("SELECT agent FROM AgentProfile agent WHERE agent.currentLoad < agent.maxCapacity AND agent.isAvailable=true")
    List<AgentProfile> findAvailabeAgents();

    @Query("SELECT agent FROM AgentProfile agent WHERE agent.currentLoad < agent.maxCapacity AND agent.isAvailable=true ORDER BY agent.currentLoad ASC")
    List<AgentProfile> findLeastLoadedAgent();
    
}
