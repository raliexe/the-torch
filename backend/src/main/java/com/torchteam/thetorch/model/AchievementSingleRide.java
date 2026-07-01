package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "achievement_single_rides")
@Getter
@Setter
@NoArgsConstructor
public class AchievementSingleRide extends Achievement {

    @Enumerated(EnumType.STRING)
    private ParamEnum parameter;

    private Double amount;
}