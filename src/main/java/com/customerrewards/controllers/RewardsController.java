package com.customerrewards.controllers;

import com.customerrewards.dto.CustomerRewardsResponse;
import com.customerrewards.services.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {
    final private RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }


    /**
     * Get rewards summary for all customers
     * GET /api/rewards
     */
    @GetMapping
    public ResponseEntity<List<CustomerRewardsResponse>> getAllCustomerRewards() {
        List<CustomerRewardsResponse> rewards = rewardsService.getCustomerRewards();
        return ResponseEntity.ok(rewards);
    }

    /**
     * Get rewards summary for a specific customer
     * GET /api/rewards/{customerId}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerRewardsResponse> getCustomerRewards(@PathVariable Long customerId) {
        try {
            CustomerRewardsResponse rewards = rewardsService.getCustomerRewards(customerId);
            return ResponseEntity.ok(rewards);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
