package com.torchteam.thetorch.integration;

import com.torchteam.thetorch.model.*;
import com.torchteam.thetorch.repository.AchievementRepository;
import com.torchteam.thetorch.repository.DecorativePieceRepository;
import com.torchteam.thetorch.repository.RiderRepository;
import com.torchteam.thetorch.repository.TeamPieceOwnershipRepository;
import com.torchteam.thetorch.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TreeNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private DecorativePieceRepository decorativePieceRepository;

    @Autowired
    private TeamPieceOwnershipRepository ownershipRepository;

    private Rider teamMember;
    private TeamPieceOwnership inventoryPiece;

    @BeforeEach
    public void setup() {
        teamMember = new Rider();
        teamMember.setName("Tree Hacker");
        teamMember.setGender("M");
        teamMember = riderRepository.save(teamMember);

        // Creates a team with a brand new, ungrown tree
        Team team = teamService.createTeam("Saplings", teamMember.getId());

        // FIX: Create and save a dummy achievement first to satisfy the DB constraint
        AchievementSingleRide dummyAchievement = new AchievementSingleRide();
        dummyAchievement.setAmount(100.0);
        dummyAchievement.setParameter(ParamEnum.DISTANCE);
        dummyAchievement = achievementRepository.save(dummyAchievement);

        // Link the piece to the achievement
        DecorativePiece mockPiece = new DecorativePiece();
        mockPiece.setAchievement(dummyAchievement);
        mockPiece = decorativePieceRepository.save(mockPiece);

        inventoryPiece = new TeamPieceOwnership();
        inventoryPiece.setTree(team.getTree());
        inventoryPiece.setPiece(mockPiece);
        inventoryPiece = ownershipRepository.save(inventoryPiece);
    }

    @Test
    public void pinPiece_ToUngrownTree_ShouldFail() throws Exception {
        mockMvc.perform(put("/api/trees/ownerships/" + inventoryPiece.getId() + "/pin")
                        .param("riderId", teamMember.getId().toString())
                        .param("posX", "10.5")
                        .param("posY", "20.5"))
                .andExpect(status().isConflict()) // 409 Conflict
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("Cannot pin decorative pieces until the tree is fully grown")));
    }
}