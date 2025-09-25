package com.customerrewards.respository;

import com.customerrewards.models.Customer;
import com.customerrewards.models.Transaction;
import com.customerrewards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testFindTransactionsByDateRange() {
        // Arrange
        Customer customer = new Customer("Test Customer", "test@email.com");
        customer = entityManager.persistAndFlush(customer);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(30);
        LocalDateTime endDate = now.plusDays(1);

        Transaction t1 = new Transaction(customer, new BigDecimal("100.00"), now.minusDays(10), "Transaction 1");
        Transaction t2 = new Transaction(customer, new BigDecimal("200.00"), now.minusDays(5), "Transaction 2");
        Transaction t3 = new Transaction(customer, new BigDecimal("300.00"), now.minusDays(40), "Transaction 3"); // Outside range

        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.persist(t3);
        entityManager.flush();

        // Act
        List<Transaction> result = transactionRepository.findTransactionsByDateRange(startDate, endDate);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getDescription().equals("Transaction 1")));
        assertTrue(result.stream().anyMatch(t -> t.getDescription().equals("Transaction 2")));
        assertFalse(result.stream().anyMatch(t -> t.getDescription().equals("Transaction 3")));
    }

    @Test
    void testFindByCustomerIdAndDateRange() {
        // Arrange
        Customer customer1 = new Customer("Customer 1", "customer1@email.com");
        Customer customer2 = new Customer("Customer 2", "customer2@email.com");
        customer1 = entityManager.persistAndFlush(customer1);
        customer2 = entityManager.persistAndFlush(customer2);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(30);
        LocalDateTime endDate = now.plusDays(1);

        Transaction t1 = new Transaction(customer1, new BigDecimal("100.00"), now.minusDays(10), "Customer 1 Transaction");
        Transaction t2 = new Transaction(customer2, new BigDecimal("200.00"), now.minusDays(5), "Customer 2 Transaction");

        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();

        // Act
        List<Transaction> result = transactionRepository.findByCustomerIdAndDateRange(customer1.getId(), startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Customer 1 Transaction", result.get(0).getDescription());
        assertEquals(customer1.getId(), result.get(0).getCustomer().getId());
    }
}
