package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    // Finds achievements where time is null (permanent) or currently active
    @Query("SELECT a FROM Achievement a WHERE " +
            "(a.timeStart IS NULL OR a.timeStart <= :now) AND " +
            "(a.timeEnd IS NULL OR a.timeEnd >= :now)")
    List<Achievement> findAllActive(LocalDateTime now);
}