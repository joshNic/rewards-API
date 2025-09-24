package com.customerrewards.services;

import com.customerrewards.dto.CustomerRewardsResponse;
import com.customerrewards.models.Customer;
import com.customerrewards.models.Transaction;
import com.customerrewards.repository.CustomerRepository;
import com.customerrewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RewardsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsService rewardsService;

    private Customer testCustomer;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John Doe", "john@test.com");
        testCustomer.setId(1L);

        LocalDateTime now = LocalDateTime.now();
        testTransactions = Arrays.asList(
                new Transaction(testCustomer, new BigDecimal("120.00"), now.minusDays(10), "Test purchase 1"),
                new Transaction(testCustomer, new BigDecimal("75.50"), now.minusDays(5), "Test purchase 2"),
                new Transaction(testCustomer, new BigDecimal("45.25"), now.minusDays(3), "Test purchase 3")
        );
    }

    @Test
    void testCalculateRewardPoints_AmountOver100() {
        // Test: $120 should give 2*20 + 1*50 = 90 points
        int points = rewardsService.calculateRewardPoints(new BigDecimal("120.00"));
        assertEquals(90, points);
    }

    @Test
    void testCalculateRewardPoints_AmountBetween50And100() {
        // Test: $75 should give 1*25 = 25 points
        int points = rewardsService.calculateRewardPoints(new BigDecimal("75.00"));
        assertEquals(25, points);
    }

    @Test
    void testCalculateRewardPoints_AmountUnder50() {
        // Test: $45 should give 0 points
        int points = rewardsService.calculateRewardPoints(new BigDecimal("45.00"));
        assertEquals(0, points);
    }

    @Test
    void testCalculateRewardPoints_Exactly100() {
        // Test: $100 should give 1*50 = 50 points
        int points = rewardsService.calculateRewardPoints(new BigDecimal("100.00"));
        assertEquals(50, points);
    }

    @Test
    void testCalculateRewardPoints_LargeAmount() {
        // Test: $350 should give 2*250 + 1*50 = 550 points
        int points = rewardsService.calculateRewardPoints(new BigDecimal("350.00"));
        assertEquals(550, points);
    }

    @Test
    void testGetCustomerRewards_ValidCustomerId() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testTransactions);

        // Act
        CustomerRewardsResponse response = rewardsService.getCustomerRewards(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals("John Doe", response.getCustomerName());
        assertTrue(response.getTotalPoints() > 0);

        // Verify method calls
        verify(customerRepository).findById(1L);
        verify(transactionRepository).findByCustomerIdAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetCustomerRewards_InvalidCustomerId() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> rewardsService.getCustomerRewards(999L));

        assertEquals("Customer not found with id: 999", exception.getMessage());
    }

    @Test
    void testGetCustomerRewards_AllCustomers() {
        // Arrange
        when(transactionRepository.findTransactionsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testTransactions);

        // Act
        List<CustomerRewardsResponse> responses = rewardsService.getCustomerRewards();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.get(0).getCustomerName());

        verify(transactionRepository).findTransactionsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
