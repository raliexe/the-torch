package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.dto.CreateTeamDto;
import com.torchteam.thetorch.dto.CreatedTeamDto;
import com.torchteam.thetorch.dto.LeaveTeamDto;
import com.torchteam.thetorch.dto.TeamLeaderboardDto;
import com.torchteam.thetorch.model.Team;
import com.torchteam.thetorch.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "http://localhost:4200")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<CreatedTeamDto> createTeam(@RequestBody CreateTeamDto request) {
        if (request.getName() == null || request.getName().isBlank() || request.getOwnerId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Team createdTeam = teamService.createTeam(request.getName().trim(), request.getOwnerId());
        CreatedTeamDto response = new CreatedTeamDto(
                createdTeam.getId(),
                createdTeam.getName(),
                createdTeam.getCoefficient()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID teamId, @RequestParam UUID requesterId) {
        teamService.deleteTeam(teamId, requesterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/leave")
    public ResponseEntity<Void> leaveTeam(@PathVariable UUID teamId, @RequestBody LeaveTeamDto request) {
        teamService.leaveTeam(teamId, request.getRiderId(), request.getReplacementAdminId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/members/{targetId}")
    public ResponseEntity<Void> kickMember(@PathVariable UUID teamId, @PathVariable UUID targetId, @RequestParam UUID adminId) {
        teamService.kickMember(teamId, adminId, targetId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{teamId}/admin")
    public ResponseEntity<Void> changeAdmin(@PathVariable UUID teamId, @RequestParam UUID currentAdminId, @RequestParam UUID newAdminId) {
        teamService.changeAdmin(teamId, currentAdminId, newAdminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member/{riderId}")
    public ResponseEntity<TeamLeaderboardDto> getTeamForMember(@PathVariable UUID riderId) {
        TeamLeaderboardDto team = teamService.getTeamForRider(riderId);
        return ResponseEntity.ok(team);
    }
}