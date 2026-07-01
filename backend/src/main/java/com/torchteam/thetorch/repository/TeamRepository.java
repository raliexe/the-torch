package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.dto.TeamLeaderboardDto;
import com.torchteam.thetorch.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    @Query("SELECT new com.torchteam.thetorch.dto.TeamLeaderboardDto(" +
            "t.id, t.name, CAST(COALESCE(SUM(ra.points), 0.0) AS double), CAST(COALESCE(SUM(r.distance), 0.0) AS double)) " +
            "FROM Team t LEFT JOIN RideAttribution ra ON t.id = ra.team.id LEFT JOIN Ride r ON ra.ride.id = r.id " +
            "GROUP BY t.id, t.name " +
            "ORDER BY CAST(COALESCE(SUM(ra.points), 0.0) AS double) DESC")
    List<TeamLeaderboardDto> getGlobalTeamLeaderboard();

    @Query("SELECT new com.torchteam.thetorch.dto.TeamLeaderboardDto(" +
            "t.id, t.name, CAST(COALESCE(SUM(ra.points), 0.0) AS double), CAST(COALESCE(SUM(r.distance), 0.0) AS double)) " +
            "FROM Team t " +
            "LEFT JOIN RideAttribution ra ON t.id = ra.team.id " +
            "LEFT JOIN Ride r ON ra.ride.id = r.id " +
            "WHERE t.id = :teamId " +
            "GROUP BY t.id, t.name")
    TeamLeaderboardDto getTeamLeaderboardById(@Param("teamId") UUID teamId);
    Optional<Team> findByTreeId(UUID id);
}