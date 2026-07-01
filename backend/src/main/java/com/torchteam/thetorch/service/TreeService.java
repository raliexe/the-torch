package com.torchteam.thetorch.service;

import com.torchteam.thetorch.dto.TreeOrnamentDto;
import com.torchteam.thetorch.model.Team;
import com.torchteam.thetorch.model.TeamPieceOwnership;
import com.torchteam.thetorch.model.Tree;
import com.torchteam.thetorch.repository.MembershipRepository;
import com.torchteam.thetorch.repository.TeamPieceOwnershipRepository;
import com.torchteam.thetorch.repository.TeamRepository;
import com.torchteam.thetorch.repository.TreeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TreeService {

    public static final double POINTS_PER_PIECE = 10.0;

    private final TreeRepository treeRepository;
    private final TeamRepository teamRepository;
    private final TeamPieceOwnershipRepository ownershipRepository;
    private final RideAttributionService attributionService;
    private final MembershipRepository membershipRepository;
    private final Random random = new Random();

    public TreeService(TreeRepository treeRepository, TeamRepository teamRepository,
                       TeamPieceOwnershipRepository ownershipRepository,
                       RideAttributionService attributionService,
                       MembershipRepository membershipRepository) {
        this.treeRepository = treeRepository;
        this.teamRepository = teamRepository;
        this.ownershipRepository = ownershipRepository;
        this.attributionService = attributionService;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public void evaluateGrowth(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        Tree tree = team.getTree();

        Double totalPoints = attributionService.getTeamTotalPoints(teamId);
        int targetPieces = (int) Math.floor(totalPoints / POINTS_PER_PIECE);
        int currentPieces = tree.getRoots() + tree.getTrunks() + tree.getBranches() + tree.getLeaves();

        if (targetPieces > currentPieces) {
            int piecesToGrant = targetPieces - currentPieces;

            for (int i = 0; i < piecesToGrant; i++) {
                grantNextPiece(tree);
            }

            // Check if it just reached "fully grown" status
            if (!tree.isGrown() && hasRequiredBasePieces(tree)) {
                tree.setGrown(true);
            }

            treeRepository.save(tree);
        }
    }

    private void grantNextPiece(Tree tree) {
        // 1. Enforce the required base sequence: 1 Root, 1 Trunk, 4 Branches, 12 Leaves
        if (tree.getRoots() < 1) {
            tree.setRoots(tree.getRoots() + 1);
        } else if (tree.getTrunks() < 1) {
            tree.setTrunks(tree.getTrunks() + 1);
        } else if (tree.getBranches() < 4) {
            tree.setBranches(tree.getBranches() + 1);
        } else if (tree.getLeaves() < 12) {
            tree.setLeaves(tree.getLeaves() + 1);
        } else {
            // 2. Base sequence complete, generate randomly (equal probability for now)
            int choice = random.nextInt(4);
            switch (choice) {
                case 0 -> tree.setRoots(tree.getRoots() + 1);
                case 1 -> tree.setTrunks(tree.getTrunks() + 1);
                case 2 -> tree.setBranches(tree.getBranches() + 1);
                case 3 -> tree.setLeaves(tree.getLeaves() + 1);
            }
        }
    }

    private boolean hasRequiredBasePieces(Tree tree) {
        return tree.getRoots() >= 1 && tree.getTrunks() >= 1 &&
                tree.getBranches() >= 4 && tree.getLeaves() >= 12;
    }

    @Transactional
    public TreeOrnamentDto pinPiece(UUID ownershipId, UUID riderId, double posX, double posY) {
        TeamPieceOwnership ownership = ownershipRepository.findById(ownershipId).orElseThrow();
        Tree tree = ownership.getTree();
        Team team = teamRepository.findByTreeId(tree.getId()).orElseThrow();

        if (!tree.isGrown()) {
            throw new IllegalStateException("Cannot pin decorative pieces until the tree is fully grown.");
        }

        validateMembership(team.getId(), riderId);

        ownership.setPosX(posX);
        ownership.setPosY(posY);

        // FIX: Map the saved entity to the DTO before returning
        return mapToDto(ownershipRepository.save(ownership));
    }

    @Transactional
    public TreeOrnamentDto unpinPiece(UUID ownershipId, UUID riderId) {
        TeamPieceOwnership ownership = ownershipRepository.findById(ownershipId).orElseThrow();
        Team team = teamRepository.findByTreeId(ownership.getTree().getId()).orElseThrow();

        validateMembership(team.getId(), riderId);

        ownership.setPosX(null);
        ownership.setPosY(null);

        // FIX: Map the saved entity to the DTO before returning
        return mapToDto(ownershipRepository.save(ownership));
    }

    private void validateMembership(UUID teamId, UUID riderId) {
        if (!membershipRepository.existsByTeamIdAndRiderId(teamId, riderId)) {
            throw new IllegalArgumentException("Only active team members can edit the tree.");
        }
    }

    public List<TreeOrnamentDto> getPinnedPieces(UUID treeId) {
        return ownershipRepository.findByTreeIdAndPosXIsNotNullAndPosYIsNotNull(treeId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TreeOrnamentDto> getInventory(UUID treeId) {
        return ownershipRepository.findByTreeIdAndPosXIsNullAndPosYIsNull(treeId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private TreeOrnamentDto mapToDto(TeamPieceOwnership ownership) {
        return new TreeOrnamentDto(
                ownership.getId(),
                ownership.getPiece().getId(),
                ownership.getPosX(),
                ownership.getPosY()
        );
    }
}