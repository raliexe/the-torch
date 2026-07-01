package com.torchteam.thetorch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TreeOrnamentDto {
    private UUID ownershipId;
    private UUID pieceId; // Used by the frontend to fetch the image later
    private Double posX;
    private Double posY;
}