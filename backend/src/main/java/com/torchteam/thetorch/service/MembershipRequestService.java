package com.torchteam.thetorch.service;

import com.torchteam.thetorch.model.MembershipRequest;
import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.model.Team;
import com.torchteam.thetorch.repository.MembershipRequestRepository;
import com.torchteam.thetorch.repository.RiderRepository;
import com.torchteam.thetorch.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipRequestService {

    private final MembershipRequestRepository requestRepository;
    private final TeamRepository teamRepository;
    private final RiderRepository riderRepository;
    private final TeamService teamService;

    public MembershipRequestService(MembershipRequestRepository requestRepository,
                                    TeamRepository teamRepository, RiderRepository riderRepository,
                                    TeamService teamService) {
        this.requestRepository = requestRepository;
        this.teamRepository = teamRepository;
        this.riderRepository = riderRepository;
        this.teamService = teamService;
    }

    public List<MembershipRequest> getInboundRequestsForTeam(UUID teamId) {
        return requestRepository.findByTeamId(teamId);
    }

    public List<MembershipRequest> getOutboundRequestsForRider(UUID riderId) {
        return requestRepository.findByRiderId(riderId);
    }

    @Transactional
    public MembershipRequest submitRequest(UUID teamId, UUID riderId) {
        if (requestRepository.existsByTeamIdAndRiderId(teamId, riderId)) {
            throw new IllegalStateException("Request already pending");
        }

        Team team = teamRepository.findById(teamId).orElseThrow();
        Rider rider = riderRepository.findById(riderId).orElseThrow();

        MembershipRequest request = new MembershipRequest();
        request.setTeam(team);
        request.setRider(rider);
        return requestRepository.save(request);
    }

    @Transactional
    public void approveRequest(UUID requestId, UUID adminId) {
        MembershipRequest request = requestRepository.findById(requestId).orElseThrow();

        if (!request.getTeam().getOwner().getId().equals(adminId)) {
            throw new IllegalArgumentException("Only the admin can approve requests");
        }

        // teamService.addMember handles coefficient recalculation and max size checks
        teamService.addMember(request.getTeam(), request.getRider());
        requestRepository.delete(request);
    }

    @Transactional
    public void denyRequest(UUID requestId, UUID adminId) {
        MembershipRequest request = requestRepository.findById(requestId).orElseThrow();
        if (!request.getTeam().getOwner().getId().equals(adminId)) {
            throw new IllegalArgumentException("Only the admin can deny requests");
        }
        requestRepository.delete(request);
    }
}