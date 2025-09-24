package com.customerrewards.config;

import com.customerrewards.models.Customer;
import com.customerrewards.models.Transaction;
import com.customerrewards.repository.CustomerRepository;
import com.customerrewards.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader  implements CommandLineRunner {


    final private CustomerRepository customerRepository;
    final private TransactionRepository transactionRepository;
    public DataLoader(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Create sample customers
        Customer alice = new Customer("Alice Johnson", "alice@email.com");
        Customer bob = new Customer("Bob Smith", "bob@email.com");
        Customer charlie = new Customer("Charlie Brown", "charlie@email.com");

        List<Customer> customers = customerRepository.saveAll(Arrays.asList(alice, bob, charlie));

        // Create sample transactions over the last 3 months
        LocalDateTime now = LocalDateTime.now();

        // Alice's transactions
        transactionRepository.saveAll(Arrays.asList(
                // Month 1 (2 months ago)
                new Transaction(alice, new BigDecimal("120.00"), now.minusMonths(2).minusDays(15), "Electronics purchase"),
                new Transaction(alice, new BigDecimal("75.50"), now.minusMonths(2).minusDays(10), "Grocery shopping"),
                new Transaction(alice, new BigDecimal("45.25"), now.minusMonths(2).minusDays(5), "Coffee and snacks"),

                // Month 2 (1 month ago)
                new Transaction(alice, new BigDecimal("200.00"), now.minusMonths(1).minusDays(20), "Clothing purchase"),
                new Transaction(alice, new BigDecimal("85.75"), now.minusMonths(1).minusDays(12), "Restaurant dinner"),
                new Transaction(alice, new BigDecimal("150.00"), now.minusMonths(1).minusDays(8), "Home improvement"),

                // Month 3 (current month)
                new Transaction(alice, new BigDecimal("95.00"), now.minusDays(10), "Gas and car supplies"),
                new Transaction(alice, new BigDecimal("125.50"), now.minusDays(5), "Online shopping")
        ));

        // Bob's transactions
        transactionRepository.saveAll(Arrays.asList(
                // Month 1
                new Transaction(bob, new BigDecimal("250.00"), now.minusMonths(2).minusDays(18), "Laptop accessories"),
                new Transaction(bob, new BigDecimal("60.00"), now.minusMonths(2).minusDays(14), "Books"),
                new Transaction(bob, new BigDecimal("35.80"), now.minusMonths(2).minusDays(8), "Lunch"),

                // Month 2
                new Transaction(bob, new BigDecimal("180.25"), now.minusMonths(1).minusDays(25), "Sports equipment"),
                new Transaction(bob, new BigDecimal("90.00"), now.minusMonths(1).minusDays(15), "Pharmacy"),

                // Month 3
                new Transaction(bob, new BigDecimal("110.00"), now.minusDays(12), "Utility bills"),
                new Transaction(bob, new BigDecimal("70.50"), now.minusDays(6), "Subscription services"),
                new Transaction(bob, new BigDecimal("320.00"), now.minusDays(3), "Furniture purchase")
        ));

        // Charlie's transactions
        transactionRepository.saveAll(Arrays.asList(
                // Month 1
                new Transaction(charlie, new BigDecimal("80.00"), now.minusMonths(2).minusDays(22), "Grocery shopping"),
                new Transaction(charlie, new BigDecimal("150.75"), now.minusMonths(2).minusDays(16), "Medical expenses"),
                new Transaction(charlie, new BigDecimal("40.00"), now.minusMonths(2).minusDays(11), "Entertainment"),

                // Month 2
                new Transaction(charlie, new BigDecimal("220.00"), now.minusMonths(1).minusDays(28), "Travel booking"),
                new Transaction(charlie, new BigDecimal("55.25"), now.minusMonths(1).minusDays(18), "Pet supplies"),
                new Transaction(charlie, new BigDecimal("130.00"), now.minusMonths(1).minusDays(9), "Home repairs"),

                // Month 3
                new Transaction(charlie, new BigDecimal("95.50"), now.minusDays(15), "Insurance payment"),
                new Transaction(charlie, new BigDecimal("175.00"), now.minusDays(8), "Professional services"),
                new Transaction(charlie, new BigDecimal("65.75"), now.minusDays(2), "Food delivery")
        ));

        System.out.println("Sample data loaded successfully!");
        System.out.println("API endpoint available at: http://localhost:8080/api/rewards");
    }

}
