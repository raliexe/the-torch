package com.torchteam.thetorch.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class MembershipId implements Serializable {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID riderId;
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID teamId;

    // equals() and hashCode() are REQUIRED for @Embeddable composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MembershipId that)) return false;
        return Objects.equals(riderId, that.riderId) && Objects.equals(teamId, that.teamId);
    }
    @Override
    public int hashCode() { return Objects.hash(riderId, teamId); }
}
