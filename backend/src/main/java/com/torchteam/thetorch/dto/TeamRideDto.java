package com.torchteam.thetorch.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamRideDto {
    private UUID rideId;
    private Double distance;
    private Double vertical;
    private LocalDateTime startDate;
    private Double pointsContributed;
}