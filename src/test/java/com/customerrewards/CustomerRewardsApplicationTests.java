package com.customerrewards;

import com.customerrewards.dto.CustomerRewardsResponse;
import com.customerrewards.models.Customer;
import com.customerrewards.models.Transaction;
import com.customerrewards.repository.CustomerRepository;
import com.customerrewards.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerRewardsApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Clear existing data
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        // Create test customer
        testCustomer = customerRepository.save(new Customer("Integration Test Customer", "integration@test.com"));

        // Create test transactions
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = Arrays.asList(
                new Transaction(testCustomer, new BigDecimal("120.00"), now.minusDays(10), "Test purchase 1"), // 90 points
                new Transaction(testCustomer, new BigDecimal("75.50"), now.minusDays(5), "Test purchase 2"),   // 25 points
                new Transaction(testCustomer, new BigDecimal("200.00"), now.minusDays(3), "Test purchase 3")   // 250 points
        );

        transactionRepository.saveAll(transactions);
    }

    @Test
    void testGetAllCustomerRewards_Integration() {
        // Act
        String url = "http://localhost:" + port + "/api/rewards";
        ResponseEntity<List<CustomerRewardsResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CustomerRewardsResponse>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        CustomerRewardsResponse customerResponse = response.getBody().get(0);
        assertEquals(testCustomer.getId(), customerResponse.getCustomerId());
        assertEquals("Integration Test Customer", customerResponse.getCustomerName());
        assertEquals(365, customerResponse.getTotalPoints()); // 90 + 25 + 250
    }

    @Test
    void testGetSpecificCustomerRewards_Integration() {
        // Act
        String url = "http://localhost:" + port + "/api/rewards/" + testCustomer.getId();
        ResponseEntity<CustomerRewardsResponse> response = restTemplate.getForEntity(url, CustomerRewardsResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        CustomerRewardsResponse customerResponse = response.getBody();
        assertEquals(testCustomer.getId(), customerResponse.getCustomerId());
        assertEquals("Integration Test Customer", customerResponse.getCustomerName());
        assertEquals(365, customerResponse.getTotalPoints()); // 90 + 25 + 250
        assertNotNull(customerResponse.getMonthlyPoints());
    }

    @Test
    void testGetNonExistentCustomerRewards_Integration() {
        // Act
        String url = "http://localhost:" + port + "/api/rewards/999";
        ResponseEntity<CustomerRewardsResponse> response = restTemplate.getForEntity(url, CustomerRewardsResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
