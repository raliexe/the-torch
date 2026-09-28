package com.torchteam.thetorch.service;

import com.torchteam.thetorch.dto.StravaActivityDto;
import com.torchteam.thetorch.dto.StravaTokenResponseDto;
import com.torchteam.thetorch.exception.IncompleteAccountException;
import com.torchteam.thetorch.model.Membership;
import com.torchteam.thetorch.model.Ride;
import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.repository.MembershipRepository;
import com.torchteam.thetorch.repository.RideRepository;
import com.torchteam.thetorch.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class StravaService {

    private final RestClient restClient;
    private final RiderRepository riderRepository;
    private final RideRepository rideRepository;
    private final MembershipRepository membershipRepository;
    private final RideAttributionService rideAttributionService;
    private final AchievementEvaluationService achievementEvaluationService;
    private final TreeService treeService;

    @Value("${strava.client-id}")
    private String clientId;

    @Value("${strava.client-secret}")
    private String clientSecret;

    public StravaService(RiderRepository riderRepository,
                         RideRepository rideRepository,
                         RideAttributionService rideAttributionService,
                         AchievementEvaluationService achievementEvaluationService,
                         MembershipRepository membershipRepository,
                         TreeService treeService) {
        this.restClient = RestClient.create(); // Instantiate it directly here
        this.riderRepository = riderRepository;
        this.rideRepository = rideRepository;
        this.rideAttributionService = rideAttributionService;
        this.achievementEvaluationService = achievementEvaluationService;
        this.membershipRepository = membershipRepository;
        this.treeService = treeService;
    }

    @Transactional
    public void connectAccount(UUID riderId, String authCode) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", authCode);
        body.add("grant_type", "authorization_code");

        // Fluent RestClient API replacing RestTemplate
        ResponseEntity<StravaTokenResponseDto> response = restClient.post()
                .uri("https://www.strava.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(StravaTokenResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            updateRiderTokens(rider, response.getBody());
        } else {
            throw new RuntimeException("Failed to connect to Strava");
        }
    }

    @Transactional
    public int syncRides(UUID riderId) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found"));

        if (rider.getStravaRefreshToken() == null) {
            throw new IllegalStateException("Rider is not connected to Strava");
        }

        String validAccessToken = getValidAccessToken(rider);

        // Calculate epoch timestamp for 72 hours ago
        long afterEpoch = Instant.now().minusSeconds(72 * 3600).getEpochSecond();
        String url = "https://www.strava.com/api/v3/athlete/activities?after=" + afterEpoch;

        // Fluent GET request with Bearer Auth
        ResponseEntity<StravaActivityDto[]> response = restClient.get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(validAccessToken))
                .retrieve()
                .toEntity(StravaActivityDto[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch activities from Strava");
        }

        int importedCount = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        for (StravaActivityDto activity : response.getBody()) {
            if (!rideRepository.existsByStravaActivityId(activity.getId())) {
                Ride ride = new Ride();
                ride.setRider(rider);
                ride.setStravaActivityId(activity.getId());
                ride.setDistance(activity.getDistance());
                ride.setVertical(activity.getTotalElevationGain());

                // Parse Strava's date format
                ride.setStartDate(LocalDateTime.parse(activity.getStartDateLocal().replace("Z", ""), formatter));

                if (activity.getMap() != null && activity.getMap().containsKey("summary_polyline")) {
                    ride.setSummaryPolyline(activity.getMap().get("summary_polyline"));
                }

                rideRepository.save(ride);

                rideAttributionService.attributeRideToTeam(ride);
                achievementEvaluationService.evaluateRideForAchievements(ride);
                importedCount++;
            }
        }
        Membership membership = membershipRepository.findFirstByRiderId(riderId)
                .orElseThrow(() -> new IncompleteAccountException("Rider not in any team"));
        treeService.evaluateGrowth(membership.getTeam().getId());

        return importedCount;
    }

    private String getValidAccessToken(Rider rider) {
        // Adding a 60-second buffer to ensure the token doesn't expire mid-request
        if (Instant.now().getEpochSecond() >= (rider.getStravaTokenExpiresAt() - 60)) {
            return refreshAccessToken(rider);
        }
        return rider.getStravaAccessToken();
    }

    private String refreshAccessToken(Rider rider) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", rider.getStravaRefreshToken());
        body.add("grant_type", "refresh_token");

        // Fluent RestClient POST for refreshing the token
        ResponseEntity<StravaTokenResponseDto> response = restClient.post()
                .uri("https://www.strava.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(StravaTokenResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            updateRiderTokens(rider, response.getBody());
            return response.getBody().getAccessToken();
        } else {
            throw new RuntimeException("Failed to refresh Strava token. User may need to reconnect.");
        }
    }

    private void updateRiderTokens(Rider rider, StravaTokenResponseDto dto) {
        rider.setStravaAccessToken(dto.getAccessToken());
        rider.setStravaRefreshToken(dto.getRefreshToken());
        rider.setStravaTokenExpiresAt(dto.getExpiresAt());
        riderRepository.save(rider);
    }
}