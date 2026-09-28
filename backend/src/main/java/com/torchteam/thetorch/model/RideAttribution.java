package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_attributions")
@Getter
@Setter
@NoArgsConstructor
public class RideAttribution {

    @EmbeddedId
    private RideAttributionId id = new RideAttributionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rideId")
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("riderId")
    @JoinColumn(name="rider_id")
    private Rider rider;

    @Column(nullable = false)
    private Double points;
}