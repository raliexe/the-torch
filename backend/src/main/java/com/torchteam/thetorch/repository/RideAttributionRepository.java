package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.dto.RiderContributionDto;
import com.torchteam.thetorch.dto.RiderStatsDto;
import com.torchteam.thetorch.dto.TeamRideDto;
import com.torchteam.thetorch.model.RideAttribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideAttributionRepository extends JpaRepository<RideAttribution, UUID> {
    @Query("SELECT COALESCE(SUM(ra.points), 0.0) FROM RideAttribution ra WHERE ra.team.id = :teamId")
    Double sumPointsByTeamId(UUID teamId);

    @Query("SELECT COALESCE(SUM(ra.points), 0.0) FROM RideAttribution ra WHERE ra.team.id = :teamId AND ra.rider.id = :riderId")
    Double sumPointsByTeamIdAndRiderId(UUID teamId, UUID riderId);

    @Query("SELECT COALESCE(SUM(ra.points), 0.0) FROM RideAttribution ra WHERE ra.rider.id = :riderId")
    Double sumPointsByRiderId(UUID riderId);

    @Query("SELECT new com.torchteam.thetorch.dto.TeamRideDto(ra.ride.id, ra.ride.distance, ra.ride.vertical, ra.ride.startDate, ra.points) FROM RideAttribution ra WHERE ra.team.id = :teamId")
    List<TeamRideDto> findRidesAndPointsByTeamId(UUID teamId);

    @Query("SELECT new com.torchteam.thetorch.dto.RiderContributionDto(" +
            "ra.rider.id, ra.rider.name, CAST(COALESCE(SUM(ra.points), 0.0) AS double)) " +
            "FROM RideAttribution ra " +
            "WHERE ra.team.id = :teamId " +
            "GROUP BY ra.rider.id, ra.rider.name " +
            "ORDER BY CAST(COALESCE(SUM(ra.points), 0.0) AS double) DESC")
    List<RiderContributionDto> getRiderContributionsForTeam(UUID teamId);

    // Makes sense even with one team per rider - get all contributions of a rider who changed teams
    @Query("SELECT new com.torchteam.thetorch.dto.RiderContributionDto(" +
            "r.id, " +
            "r.name, " +
            "CAST(COALESCE((SELECT SUM(ride.distance) FROM Ride ride WHERE ride.rider.id = r.id), 0.0) AS double), " +
            "CAST(COALESCE((SELECT SUM(ra.points) FROM RideAttribution ra WHERE ra.rider.id = r.id), 0.0) AS double)) " +
            "FROM Rider r " +
            "ORDER BY CAST(COALESCE((SELECT SUM(ra.points) FROM RideAttribution ra WHERE ra.rider.id = r.id), 0.0) AS double) DESC")
    List<RiderContributionDto> getGlobalRiderContributions();

    @Query("SELECT new com.torchteam.thetorch.dto.RiderStatsDto(" +
            "CAST(COALESCE(SUM(ra.ride.distance), 0.0) AS double), " +
            "CAST(COALESCE(SUM(ra.ride.vertical), 0.0) AS double)) " +
            "FROM RideAttribution ra " +
            "WHERE ra.team.id = :teamId " +
            "AND ra.ride.startDate >= :windowStart " +
            "AND ra.ride.startDate <= :windowEnd")
    RiderStatsDto getTeamStatsInTimeWindow(UUID teamId, LocalDateTime windowStart, LocalDateTime windowEnd);
}
