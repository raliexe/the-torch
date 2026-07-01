package com.torchteam.thetorch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderContributionDto {
    private UUID riderId;
    private String riderName;
    private Double distance;
    private Double contributedPoints;

    public RiderContributionDto(UUID riderId, String riderName, Double contributedPoints) {
        this.riderId = riderId;
        this.riderName = riderName;
        this.contributedPoints = (double) contributedPoints;
    }
}