package com.torchteam.thetorch.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;

@Embeddable
public class RideAttributionId implements Serializable {
    private UUID teamId;
    private UUID rideId;
    private UUID riderId;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RideAttributionId that)) return false;
        return Objects.equals(teamId, that.teamId) &&
                Objects.equals(rideId, that.rideId) &&
                Objects.equals(riderId, that.riderId);
    }
    @Override
    public int hashCode() { return super.hashCode(); }
}