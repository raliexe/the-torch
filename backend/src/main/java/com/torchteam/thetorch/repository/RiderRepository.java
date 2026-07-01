package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID> {
    Rider findByName(String name);
}