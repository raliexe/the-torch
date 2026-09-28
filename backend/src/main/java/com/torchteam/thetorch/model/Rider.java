package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "riders")
@Getter
@Setter
@NoArgsConstructor
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tuid_token", length = 250, nullable = true) //temporarily nullable
    private String tuidToken;

    @Column(length = 5, nullable = false)
    private String gender;

    @Column(name = "strava_access_token")
    private String stravaAccessToken;

    @Column(name = "strava_refresh_token")
    private String stravaRefreshToken;

    @Column(name = "strava_token_expires_at")
    private Long stravaTokenExpiresAt;

    private Double coefficient;

    public Rider(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
}