package com.torchteam.thetorch.service;

import com.torchteam.thetorch.dto.RiderStatsDto;
import com.torchteam.thetorch.dto.TeamRideDto;
import com.torchteam.thetorch.exception.IncompleteAccountException;
import com.torchteam.thetorch.model.Membership;
import com.torchteam.thetorch.model.Ride;
import com.torchteam.thetorch.model.RideAttribution;
import com.torchteam.thetorch.repository.MembershipRepository;
import com.torchteam.thetorch.repository.RideAttributionRepository;
import com.torchteam.thetorch.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RideAttributionService {

    private final RideAttributionRepository attributionRepository;
    private final MembershipRepository membershipRepository;
    private final RideRepository rideRepository;

    public RideAttributionService(RideAttributionRepository attributionRepository,
                                  MembershipRepository membershipRepository,
                                  RideRepository rideRepository) {
        this.attributionRepository = attributionRepository;
        this.membershipRepository = membershipRepository;
        this.rideRepository = rideRepository;
    }

    @Transactional
    public void attributeRideToTeam(Ride ride) {
        // Find all teams this rider is currently a member of
        Membership membership = membershipRepository.findFirstByRiderId(ride.getRider().getId())
                .orElseThrow(() -> new IncompleteAccountException("Rider not in any team"));

        double points = calculatePoints(
                ride.getDistance(),
                ride.getVertical(),
                ride.getRider().getCoefficient(),
                membership.getTeam().getCoefficient()
        );

        RideAttribution attribution = new RideAttribution();
        attribution.setTeam(membership.getTeam());
        attribution.setRide(ride);
        attribution.setRider(ride.getRider());
        attribution.setPoints(points);

        attributionRepository.save(attribution);

    }

    /**
     * TODO: Implement your custom point calculation logic here.
     */
    private double calculatePoints(Double distance, Double vertical, Double riderCoefficient, Double teamCoefficient) {
        // Your logic goes here
        return 1.0;
    }

    // --- Query Wrappers ---

    public Double getTeamTotalPoints(UUID teamId) {
        return attributionRepository.sumPointsByTeamId(teamId);
    }

    public Double getRiderPointsForTeam(UUID teamId, UUID riderId) {
        return attributionRepository.sumPointsByTeamIdAndRiderId(teamId, riderId);
    }

    public Double getRiderTotalPoints(UUID riderId) {
        return attributionRepository.sumPointsByRiderId(riderId);
    }

    public RiderStatsDto getRiderTotalDistanceAndVertical(UUID riderId) {
        return rideRepository.getTotalStatsByRiderId(riderId);
    }

    public List<TeamRideDto> getTeamRidesWithPoints(UUID teamId) {
        return attributionRepository.findRidesAndPointsByTeamId(teamId);
    }
}