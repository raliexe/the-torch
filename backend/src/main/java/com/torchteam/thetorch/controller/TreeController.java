package com.torchteam.thetorch.controller;

import com.torchteam.thetorch.dto.TreeOrnamentDto;
import com.torchteam.thetorch.model.TeamPieceOwnership;
import com.torchteam.thetorch.model.Tree;
import com.torchteam.thetorch.repository.TeamRepository;
import com.torchteam.thetorch.service.TreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trees")
public class TreeController {

    private final TreeService treeService;
    private final TeamRepository teamRepository;

    public TreeController(TreeService treeService, TeamRepository teamRepository) {
        this.treeService = treeService;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Tree> getTeamTree(@PathVariable UUID teamId) {
        return teamRepository.findById(teamId)
                .map(team -> ResponseEntity.ok(team.getTree()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{treeId}/inventory")
    public ResponseEntity<List<TreeOrnamentDto>> getInventory(@PathVariable UUID treeId) {
        return ResponseEntity.ok(treeService.getInventory(treeId));
    }

    @GetMapping("/{treeId}/pinned")
    public ResponseEntity<List<TreeOrnamentDto>> getPinnedPieces(@PathVariable UUID treeId) {
        return ResponseEntity.ok(treeService.getPinnedPieces(treeId));
    }

    @PutMapping("/ownerships/{ownershipId}/pin")
    public ResponseEntity<TreeOrnamentDto> pinPiece(@PathVariable UUID ownershipId,
                                                       @RequestParam UUID riderId,
                                                       @RequestParam double posX,
                                                       @RequestParam double posY) {
        return ResponseEntity.ok(treeService.pinPiece(ownershipId, riderId, posX, posY));
    }

    @PutMapping("/ownerships/{ownershipId}/unpin")
    public ResponseEntity<TreeOrnamentDto> unpinPiece(@PathVariable UUID ownershipId,
                                                         @RequestParam UUID riderId) {
        return ResponseEntity.ok(treeService.unpinPiece(ownershipId, riderId));
    }
}
