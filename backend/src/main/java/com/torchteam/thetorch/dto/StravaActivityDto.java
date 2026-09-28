package com.torchteam.thetorch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class StravaActivityDto {
    private Long id;
    private Double distance;

    @JsonProperty("total_elevation_gain")
    private Double totalElevationGain;

    @JsonProperty("start_date_local")
    private String startDateLocal;

    private Map<String, String> map; // Strava nests the polyline here
}