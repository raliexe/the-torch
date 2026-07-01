package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.dto.RiderContributionDto;
import com.torchteam.thetorch.dto.TeamLeaderboardDto;
import com.torchteam.thetorch.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    // Returns ALL teams, sorted highest to lowest points
    @GetMapping("/teams")
    public ResponseEntity<List<TeamLeaderboardDto>> getGlobalLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    // Returns ALL riders who contributed to a team, sorted highest to lowest points
    // doesn't populate Distance attribute
    @GetMapping("/teams/{teamId}/riders")
    public ResponseEntity<List<RiderContributionDto>> getTeamDetailsLeaderboard(@PathVariable UUID teamId) {
        return ResponseEntity.ok(leaderboardService.getTeamDetailsLeaderboard(teamId));
    }

    // returns all riders, sorted by points
    @GetMapping("/riders")
    public ResponseEntity<List<RiderContributionDto>> getRiderDetailsLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalRiderLeaderboard());
    }
}