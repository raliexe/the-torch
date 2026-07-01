package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.dto.RiderStatsDto;
import com.torchteam.thetorch.dto.TeamRideDto;
import com.torchteam.thetorch.service.RideAttributionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final RideAttributionService statsService;

    public StatsController(RideAttributionService statsService) {
        this.statsService = statsService;
    }

    // Number of points held by a team
    @GetMapping("/teams/{teamId}/points")
    public ResponseEntity<Map<String, Double>> getTeamPoints(@PathVariable UUID teamId) {
        return ResponseEntity.ok(Map.of("totalPoints", statsService.getTeamTotalPoints(teamId)));
    }

    // All rides which contribute to a team's score with numbers of points
    @GetMapping("/teams/{teamId}/rides")
    public ResponseEntity<List<TeamRideDto>> getTeamRides(@PathVariable UUID teamId) {
        return ResponseEntity.ok(statsService.getTeamRidesWithPoints(teamId));
    }

    // Number of points contributed by a rider to a specific team
    @GetMapping("/teams/{teamId}/riders/{riderId}/points")
    public ResponseEntity<Map<String, Double>> getRiderPointsForTeam(@PathVariable UUID teamId, @PathVariable UUID riderId) {
        return ResponseEntity.ok(Map.of("contributedPoints", statsService.getRiderPointsForTeam(teamId, riderId)));
    }

    // Number of points contributed by a rider in total across all teams
    @GetMapping("/riders/{riderId}/points")
    public ResponseEntity<Map<String, Double>> getRiderTotalPoints(@PathVariable UUID riderId) {
        return ResponseEntity.ok(Map.of("totalPoints", statsService.getRiderTotalPoints(riderId)));
    }

    // Total vertical and horizontal distance covered by a rider
    @GetMapping("/riders/{riderId}/totals")
    public ResponseEntity<RiderStatsDto> getRiderDistanceTotals(@PathVariable UUID riderId) {
        return ResponseEntity.ok(statsService.getRiderTotalDistanceAndVertical(riderId));
    }
}