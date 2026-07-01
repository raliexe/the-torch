package com.torchteam.thetorch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "trees")
@Getter
@Setter
public class Tree {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false)
    private int roots;

    @Column(nullable = false)
    private int trunks;

    @Column(nullable = false)
    private int branches;

    @Column(nullable = false)
    private int leaves;

    @Column(name = "is_grown", nullable = false)
    @JsonProperty("isGrown")
    private boolean isGrown = false;
}