package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.dto.CreateRiderRequestDto;
import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.service.RiderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/riders")
@CrossOrigin(origins = "http://localhost:4200")
public class RiderController {

    private final RiderService riderService;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @PostMapping
    public ResponseEntity<Rider> createRider(@RequestBody CreateRiderRequestDto request) {
        // Basic validation can be handled here or via @Valid annotations in the future
        if (request.getName() == null || request.getName().trim().isEmpty() || request.getGender() == null) {
            return ResponseEntity.badRequest().build();
        }

        Rider createdRider = riderService.createRider(
                request.getName(),
                request.getAge(),
                request.getGender(),
                request.getTuidToken()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRider);
    }

    @GetMapping
    public ResponseEntity<Rider> getRider(
            @RequestParam String name,
            @RequestParam(required = false) String tuidToken
    ) {
        Rider rider = riderService.getRider(name);
        if (rider == null) {
            return ResponseEntity.notFound().build();
        }

        if (tuidToken != null && !tuidToken.equals(rider.getTuidToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(rider);
    }
}