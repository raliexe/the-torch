package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.service.StravaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/strava")
public class StravaController {

    private final StravaService stravaService;

    public StravaController(StravaService stravaService) {
        this.stravaService = stravaService;
    }

    // Called by your frontend after Strava redirects back with the code
    @PostMapping("/connect/{riderId}")
    public ResponseEntity<Void> connectStrava(@PathVariable UUID riderId, @RequestParam String code) {
        stravaService.connectAccount(riderId, code);
        return ResponseEntity.ok().build();
    }

    // Called when the user logs in to fetch new rides
    @PostMapping("/sync/{riderId}")
    public ResponseEntity<Map<String, Integer>> syncRides(@PathVariable UUID riderId) {
        int importedRidesCount = stravaService.syncRides(riderId);
        return ResponseEntity.ok(Map.of("imported_rides", importedRidesCount));
    }
}