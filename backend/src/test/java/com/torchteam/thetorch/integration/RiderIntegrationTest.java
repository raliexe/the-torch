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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rolls back the database after the test
public class RiderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createRider_ShouldCalculateCoefficientCorrectly() throws Exception {
        // Base 1.0 + Age 40 (+0.1) + Gender F (+0.2) = 1.3
        CreateRiderRequestDto request = new CreateRiderRequestDto("Alice", 40, "F");

        mockMvc.perform(post("/api/riders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.gender").value("F"))
                .andExpect(jsonPath("$.coefficient").value(1.3));
    }
}