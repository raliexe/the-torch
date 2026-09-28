package com.torchteam.thetorch.service;

import com.torchteam.thetorch.dto.RiderContributionDto;
import com.torchteam.thetorch.dto.TeamLeaderboardDto;
import com.torchteam.thetorch.repository.RideAttributionRepository;
import com.torchteam.thetorch.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardService {

    private final TeamRepository teamRepository;
    private final RideAttributionRepository attributionRepository;

    public LeaderboardService(TeamRepository teamRepository, RideAttributionRepository attributionRepository) {
        this.teamRepository = teamRepository;
        this.attributionRepository = attributionRepository;
    }

    public List<TeamLeaderboardDto> getGlobalLeaderboard() {
        return teamRepository.getGlobalTeamLeaderboard();
    }

    public List<RiderContributionDto> getTeamDetailsLeaderboard(UUID teamId) {
        return attributionRepository.getRiderContributionsForTeam(teamId);
    }

    public List<RiderContributionDto> getGlobalRiderLeaderboard() {
        return attributionRepository.getGlobalRiderContributions();
    }
}