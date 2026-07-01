package com.torchteam.thetorch.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateTeamDto {
    private String name;
    private UUID ownerId;
}