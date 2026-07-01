package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "achievement_locations")
@Getter
@Setter
@NoArgsConstructor
// ID is automatically inherited and acts as a Foreign Key to `achievements` table
public class AchievementLocation extends Achievement {

    private Double latitude;
    private Double longitude; // Corrected from "long"

    @Column(name = "range_km")
    private Double rangeKm;
}