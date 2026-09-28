package com.torchteam.thetorch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamLeaderboardDto {
    private UUID teamId;
    private String teamName;
    private Double totalPoints;
    private Double totalDistance;

    public TeamLeaderboardDto(UUID teamId, String teamName, int totalPoints) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.totalPoints = (double) totalPoints;
    }
}