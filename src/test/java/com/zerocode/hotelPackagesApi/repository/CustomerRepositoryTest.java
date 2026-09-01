package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoyaltyPointRepository loyaltyPointRepository;

    private Customer testCustomer1;
    private Customer testCustomer2;
    private LoyaltyPoint loyaltyPoint;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll();
        loyaltyPointRepository.deleteAll();

        LoyaltyPoint loyaltyPoint1 = new LoyaltyPoint();
        loyaltyPoint1.setCount(100.0);
        loyaltyPoint1.setEarnedDate(LocalDate.now());
        loyaltyPoint1 = loyaltyPointRepository.save(loyaltyPoint1);

        LoyaltyPoint loyaltyPoint2 = new LoyaltyPoint();
        loyaltyPoint2.setCount(200.0);
        loyaltyPoint2.setEarnedDate(LocalDate.now());
        loyaltyPoint2 = loyaltyPointRepository.save(loyaltyPoint2);

        testCustomer1 = new Customer();
        testCustomer1.setUserName("customer1");
        testCustomer1.setPassword("password1");
        testCustomer1.setEmail("customer1@example.com");
        testCustomer1.setProfilePhotoUrl("photo1.jpg");
        testCustomer1.setLoyaltyPoint(loyaltyPoint1);

        testCustomer2 = new Customer();
        testCustomer2.setUserName("customer2");
        testCustomer2.setPassword("password2");
        testCustomer2.setEmail("customer2@example.com");
        testCustomer2.setProfilePhotoUrl("photo2.jpg");
        testCustomer2.setLoyaltyPoint(loyaltyPoint2);
    }

    @Test
    void testSaveAndFindById() {
        Customer savedCustomer = customerRepository.save(testCustomer1);
        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
        assertEquals("customer1", foundCustomer.get().getUserName());
        assertEquals("customer1@example.com", foundCustomer.get().getEmail());
        assertNotNull(foundCustomer.get().getLoyaltyPoint());
        assertEquals(100.0, foundCustomer.get().getLoyaltyPoint().getCount());
    }

    @Test
    void testFindAll() {
        customerRepository.save(testCustomer1);
        customerRepository.save(testCustomer2);
        List<Customer> customers = customerRepository.findAll();
        assertEquals(2, customers.size());
        assertTrue(customers.stream().anyMatch(c -> c.getUserName().equals("customer1")));
        assertTrue(customers.stream().anyMatch(c -> c.getUserName().equals("customer2")));
    }

    @Test
    void testDeleteCustomer() {
        Customer savedCustomer = customerRepository.save(testCustomer1);
        assertNotNull(savedCustomer.getId());
        customerRepository.delete(savedCustomer);
        Optional<Customer> deletedCustomer = customerRepository.findById(savedCustomer.getId());
        assertFalse(deletedCustomer.isPresent());
    }

    @Test
    void testCountCustomers() {
        assertEquals(0, customerRepository.count());
        customerRepository.save(testCustomer1);
        customerRepository.save(testCustomer2);
        assertEquals(2, customerRepository.count());
    }

    @Test
    void testFindByEmail() {
        customerRepository.save(testCustomer1);
        Optional<Customer> foundCustomer = customerRepository.findByEmail("customer1@example.com");
        assertTrue(foundCustomer.isPresent());
        assertEquals("customer1", foundCustomer.get().getUserName());
        assertEquals("customer1@example.com", foundCustomer.get().getEmail());

        Optional<Customer> notFound = customerRepository.findByEmail("notfound@example.com");
        assertFalse(notFound.isPresent());
    }
} 