package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.TeamPieceOwnership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamPieceOwnershipRepository extends JpaRepository<TeamPieceOwnership, UUID> {
    List<TeamPieceOwnership> findByTreeIdAndPosXIsNotNullAndPosYIsNotNull(UUID treeId); // Pinned pieces
    List<TeamPieceOwnership> findByTreeIdAndPosXIsNullAndPosYIsNull(UUID treeId); // Unpinned/Inventory pieces
}