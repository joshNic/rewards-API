package com.customerrewards.dto;

import java.util.Map;

public class CustomerRewardsResponse {
    private Long customerId;
    private String customerName;
    private Map<String, Integer> monthlyPoints;
    private Integer totalPoints;

    public CustomerRewardsResponse() {}

    public CustomerRewardsResponse(Long customerId, String customerName, Map<String, Integer> monthlyPoints, Integer totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyPoints = monthlyPoints;
        this.totalPoints = totalPoints;
    }


    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Map<String, Integer> getMonthlyPoints() { return monthlyPoints; }
    public void setMonthlyPoints(Map<String, Integer> monthlyPoints) { this.monthlyPoints = monthlyPoints; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
}