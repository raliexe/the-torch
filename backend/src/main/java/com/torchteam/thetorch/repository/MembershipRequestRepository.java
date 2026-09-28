package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.MembershipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, UUID> {
    List<MembershipRequest> findByTeamId(UUID teamId);
    List<MembershipRequest> findByRiderId(UUID riderId);
    boolean existsByTeamIdAndRiderId(UUID teamId, UUID riderId);
    void deleteByTeamId(UUID teamId);
}