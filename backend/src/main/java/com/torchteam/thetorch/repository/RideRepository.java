package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.dto.RiderStatsDto;
import com.torchteam.thetorch.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    boolean existsByStravaActivityId(Long stravaActivityId);
    @Query("SELECT new com.torchteam.thetorch.dto.RiderStatsDto(" +
            "CAST(COALESCE(SUM(r.distance), 0.0) AS double), " +
            "CAST(COALESCE(SUM(r.vertical), 0.0) AS double)) " +
            "FROM Ride r WHERE r.rider.id = :riderId")
    RiderStatsDto getTotalStatsByRiderId(UUID riderId);
}