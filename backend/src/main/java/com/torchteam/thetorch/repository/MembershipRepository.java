package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.Membership;
import com.torchteam.thetorch.model.MembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipId> {
    long countByTeamId(UUID teamId);
    boolean existsByTeamIdAndRiderId(UUID teamId, UUID riderId);
    void deleteByTeamIdAndRiderId(UUID teamId, UUID riderId);
    void deleteByTeamId(UUID teamId);

    @Query("SELECT m FROM Membership m WHERE m.id.riderId = :riderId")
    Optional<Membership> findFirstByRiderId(@Param("riderId") UUID riderId);
}