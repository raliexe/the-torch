package com.torchteam.thetorch.datagenerator;

import com.torchteam.thetorch.model.MembershipRequest;
import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.model.Team;
import com.torchteam.thetorch.repository.RiderRepository;
import com.torchteam.thetorch.service.MembershipRequestService;
import com.torchteam.thetorch.service.TeamService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
@Slf4j
@RequiredArgsConstructor
public class DataGenerator {
    private final MembershipRequestService membershipRequestService;
    private final TeamService teamService;
    private final RiderRepository riderRepository;

    @PostConstruct
    private void generateData() {
        final Rider rider1 = new Rider("Steve", "male");
        final Rider rider2 = new Rider("Alex", "female");
        riderRepository.save(rider1);
        riderRepository.save(rider2);

        Team team = teamService.createTeam("Ender Dragons", rider1.getId());

        MembershipRequest membershipRequest = membershipRequestService.submitRequest(team.getId(), rider2.getId());

        membershipRequestService.approveRequest(membershipRequest.getId(), rider1.getId());
    }
}