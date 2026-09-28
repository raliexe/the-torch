package com.torchteam.thetorch.integration;

import com.torchteam.thetorch.dto.CreateRiderRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RiderNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createRider_MissingName_ShouldReturnBadRequest() throws Exception {
        CreateRiderRequestDto request = new CreateRiderRequestDto("", 30, "M"); // Empty name

        mockMvc.perform(post("/api/riders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRider_MissingGender_ShouldReturnBadRequest() throws Exception {
        CreateRiderRequestDto request = new CreateRiderRequestDto("John", 30, null); // Null gender

        mockMvc.perform(post("/api/riders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}