package com.torchteam.thetorch.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class LeaveTeamDto {
    private UUID riderId;
    private UUID replacementAdminId; // Optional, required only if admin leaves
}