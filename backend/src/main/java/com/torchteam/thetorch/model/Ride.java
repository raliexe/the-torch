package com.torchteam.thetorch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider rider;

    @Column(name = "distance")
    private Double distance;
    @Column(name = "vertical")
    private Double vertical;

    @Column(name = "strava_activity_id", unique = true)
    private Long stravaActivityId;

    @Column(name = "summary_polyline", length = 4000)
    private String summaryPolyline;

    @Column(name = "start_date")
    private LocalDateTime startDate;
}