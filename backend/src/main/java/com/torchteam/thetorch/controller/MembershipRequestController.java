package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.model.MembershipRequest;
import com.torchteam.thetorch.service.MembershipRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/membership-requests")
public class MembershipRequestController {

    private final MembershipRequestService requestService;

    public MembershipRequestController(MembershipRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<MembershipRequest> submitRequest(@RequestParam UUID teamId, @RequestParam UUID riderId) {
        MembershipRequest request = requestService.submitRequest(teamId, riderId);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MembershipRequest>> getInboundRequests(@PathVariable UUID teamId) {
        return ResponseEntity.ok(requestService.getInboundRequestsForTeam(teamId));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<MembershipRequest>> getOutboundRequests(@PathVariable UUID riderId) {
        return ResponseEntity.ok(requestService.getOutboundRequestsForRider(riderId));
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable UUID requestId, @RequestParam UUID adminId) {
        requestService.approveRequest(requestId, adminId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{requestId}/deny")
    public ResponseEntity<Void> denyRequest(@PathVariable UUID requestId, @RequestParam UUID adminId) {
        requestService.denyRequest(requestId, adminId);
        return ResponseEntity.ok().build();
    }
}