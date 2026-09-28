package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @EmbeddedId
    private MembershipId id = new MembershipId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("riderId") // Maps the riderId from MembershipId
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId") // Maps the teamId from MembershipId
    @JoinColumn(name = "team_id")
    private Team team;
}