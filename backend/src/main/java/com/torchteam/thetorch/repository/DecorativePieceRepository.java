package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.DecorativePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecorativePieceRepository extends JpaRepository<DecorativePiece, UUID> {
    Optional<DecorativePiece> findByAchievementId(UUID achievementId);
}