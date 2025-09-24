package com.customerrewards.services;

import com.customerrewards.dto.CustomerRewardsResponse;
import com.customerrewards.models.Customer;
import com.customerrewards.models.Transaction;
import com.customerrewards.repository.CustomerRepository;
import com.customerrewards.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RewardsService {

    final private CustomerRepository customerRepository;
    final private TransactionRepository transactionRepository;

    public RewardsService(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    public int calculateRewardPoints(BigDecimal amount) {
        int points = 0;
        double amountValue = amount.doubleValue();

        if (amountValue > 100) {
            // 2 points for amount over $100
            points += (int) ((amountValue - 100) * 2);
            // 1 point for the $50-$100 range
            points += 50;
        } else if (amountValue > 50) {
            // 1 point for amount between $50 and $100
            points += (int) (amountValue - 50);
        }

        return points;
    }

    /**
     * Get rewards summary for all customers in the last 3 months
     */
    public List<CustomerRewardsResponse> getCustomerRewards() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        List<Transaction> transactions = transactionRepository.findTransactionsByDateRange(startDate, endDate);

        // Group transactions by customer
        Map<Long, List<Transaction>> transactionsByCustomer = transactions.stream()

                .collect(Collectors.groupingBy(t -> t.getCustomer().getId()));

        List<CustomerRewardsResponse> responses = new ArrayList<>();

        for (Map.Entry<Long, List<Transaction>> entry : transactionsByCustomer.entrySet()) {
            Long customerId = entry.getKey();
            List<Transaction> customerTransactions = entry.getValue();
            Customer customer = customerTransactions.get(0).getCustomer();

            Map<String, Integer> monthlyPoints = new HashMap<>();
            int totalPoints = 0;

            // Group by month and calculate points
            for (Transaction transaction : customerTransactions) {
                String monthKey = transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                int points = calculateRewardPoints(transaction.getAmount());

                monthlyPoints.merge(monthKey, points, Integer::sum);
                totalPoints += points;
            }

            responses.add(new CustomerRewardsResponse(customerId, customer.getName(), monthlyPoints, totalPoints));
        }

        return responses.stream()
                .sorted(Comparator.comparing(CustomerRewardsResponse::getCustomerName))
                .collect(Collectors.toList());
    }

    /**
     * Get rewards summary for a specific customer
     */
    public CustomerRewardsResponse getCustomerRewards(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }

        Customer customer = customerOpt.get();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);

        Map<String, Integer> monthlyPoints = new HashMap<>();
        int totalPoints = 0;

        for (Transaction transaction : transactions) {
            String monthKey = transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            int points = calculateRewardPoints(transaction.getAmount());

            monthlyPoints.merge(monthKey, points, Integer::sum);
            totalPoints += points;
        }

        return new CustomerRewardsResponse(customerId, customer.getName(), monthlyPoints, totalPoints);
    }
}
