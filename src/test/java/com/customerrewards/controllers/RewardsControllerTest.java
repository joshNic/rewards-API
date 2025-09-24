package com.customerrewards.controllers;

import com.customerrewards.dto.CustomerRewardsResponse;
import com.customerrewards.services.RewardsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest
@ContextConfiguration(classes = {RewardsController.class})
@ActiveProfiles("test")
public class RewardsControllerTest {

    @Autowired
   private MockMvc mockMvc;
    @Autowired
   private ObjectMapper objectMapper;

    @MockitoBean
    private RewardsService rewardsService;


    @Test
    void testGetAllCustomerRewards_Success() throws Exception {
        // Arrange
        Map<String, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put("2024-09", 150);

        List<CustomerRewardsResponse> mockResponses = Arrays.asList(
                new CustomerRewardsResponse(1L, "Alice Johnson", monthlyPoints, 150),
                new CustomerRewardsResponse(2L, "Bob Smith", monthlyPoints, 200)
        );

        when(rewardsService.getCustomerRewards()).thenReturn(mockResponses);

        // Act & Assert
        mockMvc.perform(get("/api/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // This will show you the actual request/response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Alice Johnson"))
                .andExpect(jsonPath("$[1].customerName").value("Bob Smith"));

    }

    @Test
    void testGetCustomerRewards_SpecificCustomer() throws Exception {
        // Arrange
        Map<String, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put("2024-09", 150);

        CustomerRewardsResponse mockResponse = new CustomerRewardsResponse(1L, "Alice Johnson", monthlyPoints, 150);

        when(rewardsService.getCustomerRewards(1L)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/rewards/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice Johnson"))
                .andExpect(jsonPath("$.totalPoints").value(150));
    }

    @Test
    void testGetCustomerRewards_NotFound() throws Exception {
        // Arrange
        when(rewardsService.getCustomerRewards(999L))
                .thenThrow(new RuntimeException("Customer not found"));

        // Act & Assert
        mockMvc.perform(get("/api/rewards/{customerId}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
