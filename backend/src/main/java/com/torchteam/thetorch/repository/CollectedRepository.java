package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.Collected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollectedRepository extends JpaRepository<Collected, UUID> {
    boolean existsByAchievementIdAndTeamId(UUID achievementId, UUID teamId);
}