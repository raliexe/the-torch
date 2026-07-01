package com.torchteam.thetorch.integration;

import com.torchteam.thetorch.model.Ride;
import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.model.Team;
import com.torchteam.thetorch.repository.RideRepository;
import com.torchteam.thetorch.repository.RiderRepository;
import com.torchteam.thetorch.service.RideAttributionService;
import com.torchteam.thetorch.service.TeamService;
import com.torchteam.thetorch.service.TreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StatsAndTreeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideAttributionService rideAttributionService;

    @Autowired
    private TreeService treeService;

    private Team team;
    private Rider rider;

    @BeforeEach
    public void setup() {
        // Setup a Rider
        rider = new Rider();
        rider.setName("Cyclist");
        rider.setGender("M");
        rider.setCoefficient(1.0);
        rider = riderRepository.save(rider);

        // Setup a Team
        team = teamService.createTeam("Mountain Goats", rider.getId());

        // Temporarily change your custom calculation to return 15 points per ride for testing
        // (Assuming you hardcoded it or we just rely on your formula to generate > 10 points)
    }

    @Test
    public void testStatsLeaderboardAndTreeGrowth() throws Exception {
        // 1. Simulate a Strava Ride Import
        Ride ride = new Ride();
        ride.setRider(rider);
        ride.setDistance(50000.0); // 50km
        ride.setVertical(1000.0);
        ride.setStartDate(LocalDateTime.now());
        ride.setStravaActivityId(12345L);
        ride = rideRepository.save(ride);

        // 2. Attribute points and evaluate tree (Normally done in StravaService)
        rideAttributionService.attributeRideToTeam(ride);
        treeService.evaluateGrowth(team.getId());

        // 3. Check Leaderboard contains this team at the top after the ride
        mockMvc.perform(get("/api/leaderboard/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.teamId=='" + team.getId() + "')].teamName").value("Mountain Goats"));
        // .andExpect(jsonPath("$[0].totalPoints").value(...)); // Check your math output here

        // 4. Check Rider's total stats
        mockMvc.perform(get("/api/stats/riders/" + rider.getId() + "/totals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDistance").value(50000.0))
                .andExpect(jsonPath("$.totalVertical").value(1000.0));

        // 5. Check Tree Growth (Assuming the ride generated enough points to grow the tree)
        System.out.println(mockMvc.perform(get("/api/trees/team/" + team.getId())));
        mockMvc.perform(get("/api/trees/team/" + team.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.isGrown").value(false)); // It shouldn't be grown yet
    }
}