package com.torchteam.thetorch.service;

import com.torchteam.thetorch.dto.TeamLeaderboardDto;
import com.torchteam.thetorch.exception.IncompleteAccountException;
import com.torchteam.thetorch.model.*;
import com.torchteam.thetorch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
public class TeamService {

    public static final int MAX_TEAM_SIZE = 30; //! CHANGE HERE

    private final TeamRepository teamRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipRequestRepository membershipRequestRepository;
    private final RiderRepository riderRepository;
    private final TreeRepository treeRepository;

    public TeamService(TeamRepository teamRepository, MembershipRepository membershipRepository,
                       MembershipRequestRepository membershipRequestRepository,
                       RiderRepository riderRepository, TreeRepository treeRepository) {
        this.teamRepository = teamRepository;
        this.membershipRepository = membershipRepository;
        this.membershipRequestRepository = membershipRequestRepository;
        this.riderRepository = riderRepository;
        this.treeRepository = treeRepository;
    }

    @Transactional
    public Team createTeam(String name, UUID ownerId) {
        Rider owner = riderRepository.findById(ownerId).orElseThrow();

        if (membershipRepository.findFirstByRiderId(ownerId).isPresent()) {
            throw new IllegalStateException("Rider is already in a team");
        }

        // Generate dummy tree for new team
        Tree dummyTree = new Tree();
        dummyTree = treeRepository.save(dummyTree);

        Team team = new Team();
        team.setName(name);
        team.setOwner(owner);
        team.setTree(dummyTree);
        team.setCoefficient(1.0); // 1 member initially
        team = teamRepository.save(team);

        addMemberQuietly(team, owner);
        return team;
    }

    @Transactional
    public void deleteTeam(UUID teamId, UUID requesterId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        if (!team.getOwner().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the admin can delete the team");
        }

        membershipRequestRepository.deleteByTeamId(teamId);
        membershipRepository.deleteByTeamId(teamId);
        teamRepository.delete(team);
    }

    @Transactional
    public void leaveTeam(UUID teamId, UUID riderId, UUID replacementAdminId) {
        Team team = teamRepository.findById(teamId).orElseThrow();

        if (!membershipRepository.existsByTeamIdAndRiderId(teamId, riderId)) {
            throw new IllegalArgumentException("Rider is not in this team");
        }

        long memberCount = membershipRepository.countByTeamId(teamId);

        if (team.getOwner().getId().equals(riderId)) {
            if (memberCount == 1) {
                deleteTeam(teamId, riderId);
                return;
            }
            if (replacementAdminId == null || !membershipRepository.existsByTeamIdAndRiderId(teamId, replacementAdminId)) {
                throw new IllegalArgumentException("Admin leaving a populated team must provide a valid replacement admin ID");
            }
            changeAdmin(teamId, riderId, replacementAdminId);
        }

        membershipRepository.deleteByTeamIdAndRiderId(teamId, riderId);
        recalculateTeamCoefficient(team);
    }

    @Transactional
    public void kickMember(UUID teamId, UUID adminId, UUID targetRiderId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        if (!team.getOwner().getId().equals(adminId)) {
            throw new IllegalArgumentException("Only the admin can kick members");
        }
        if (adminId.equals(targetRiderId)) {
            throw new IllegalArgumentException("Admin cannot kick themselves. Use leave instead.");
        }

        membershipRepository.deleteByTeamIdAndRiderId(teamId, targetRiderId);
        recalculateTeamCoefficient(team);
    }

    @Transactional
    public void changeAdmin(UUID teamId, UUID currentAdminId, UUID newAdminId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        if (!team.getOwner().getId().equals(currentAdminId)) {
            throw new IllegalArgumentException("Only the current admin can hand over the role");
        }
        if (!membershipRepository.existsByTeamIdAndRiderId(teamId, newAdminId)) {
            throw new IllegalArgumentException("New admin must be a member of the team");
        }

        Rider newAdmin = riderRepository.findById(newAdminId).orElseThrow();
        team.setOwner(newAdmin);
        teamRepository.save(team);
    }

    @Transactional
    public void addMember(Team team, Rider rider) {
        if (membershipRepository.countByTeamId(team.getId()) >= MAX_TEAM_SIZE) {
            throw new IllegalStateException("Team has reached the maximum size of " + MAX_TEAM_SIZE);
        }
        if (membershipRepository.existsByTeamIdAndRiderId(team.getId(), rider.getId())) {
            throw new IllegalStateException("Rider is already in the team");
        }

        addMemberQuietly(team, rider);
        recalculateTeamCoefficient(team);
    }

  @Transactional(readOnly = true)
  public TeamLeaderboardDto getTeamForRider(UUID riderId) {
    Membership membership = membershipRepository.findFirstByRiderId(riderId)
            .orElseThrow(() -> new IncompleteAccountException("Rider is not in any team"));

    Team team = membership.getTeam();

    try {
      TeamLeaderboardDto leaderboard = teamRepository.getTeamLeaderboardById(team.getId());
      if (leaderboard != null) {
        return leaderboard;
      }
    } catch (Exception ignored) {
      // Fall back to basic team info when the stats query returns nothing.
    }

    return new TeamLeaderboardDto(team.getId(), team.getName(), 0.0, 0.0);
  }

    private void addMemberQuietly(Team team, Rider rider) {
        Membership membership = new Membership();
        MembershipId id = new MembershipId();
        id.setTeamId(team.getId());
        id.setRiderId(rider.getId());
        membership.setId(id);
        membership.setTeam(team);
        membership.setRider(rider);
        membershipRepository.saveAndFlush(membership);
    }

    private void recalculateTeamCoefficient(Team team) {
        long memberCount = membershipRepository.countByTeamId(team.getId());
        double newCoefficient = memberCount > 0 ? (1.0 / memberCount) : 1.0;
        team.setCoefficient(newCoefficient);
        teamRepository.save(team);
    }
}