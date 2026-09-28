package com.torchteam.thetorch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreatedTeamDto {
    private UUID id;
    private String name;
    private Double coefficient;
}
