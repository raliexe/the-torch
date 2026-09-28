package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "achievement_group_periods")
@Getter
@Setter
@NoArgsConstructor
public class AchievementGroupPeriod extends Achievement {

    @Enumerated(EnumType.STRING)
    private ParamEnum parameter;

    private Double amount;

    @Column(name = "duration_seconds")
    private Long durationSeconds; // Changed from time for DB safety
}