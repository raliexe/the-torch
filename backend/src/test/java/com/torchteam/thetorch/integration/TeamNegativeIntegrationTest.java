package com.torchteam.thetorch.integration;

import com.torchteam.thetorch.dto.LeaveTeamDto;
import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.model.Team;
import com.torchteam.thetorch.repository.RiderRepository;
import com.torchteam.thetorch.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TeamNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private TeamService teamService;

    private Rider admin;
    private Rider normalMember;
    private Team team;

    @BeforeEach
    public void setup() {
        admin = new Rider();
        admin.setName("Admin");
        admin.setGender("M");
        admin = riderRepository.save(admin);

        normalMember = new Rider();
        normalMember.setName("Member");
        normalMember.setGender("M");
        normalMember = riderRepository.save(normalMember);

        team = teamService.createTeam("Negative Test Team", admin.getId());
        teamService.addMember(team, normalMember);
    }

    @Test
    public void deleteTeam_AsNonAdmin_ShouldFail() throws Exception {
        mockMvc.perform(delete("/api/teams/" + team.getId())
                        .param("requesterId", normalMember.getId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Only the admin can delete")));
    }

    @Test
    public void kickMember_AsNonAdmin_ShouldFail() throws Exception {
        mockMvc.perform(delete("/api/teams/" + team.getId() + "/members/" + admin.getId())
                        .param("adminId", normalMember.getId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Only the admin can kick")));
    }

    @Test
    public void adminLeaveTeam_WithoutReplacement_ShouldFail() throws Exception {
        LeaveTeamDto request = new LeaveTeamDto();
        request.setRiderId(admin.getId());
        request.setReplacementAdminId(null); // Explicitly missing

        mockMvc.perform(post("/api/teams/" + team.getId() + "/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("must provide a valid replacement")));
    }
}