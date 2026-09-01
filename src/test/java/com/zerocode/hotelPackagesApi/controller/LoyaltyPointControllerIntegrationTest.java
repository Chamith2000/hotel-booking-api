package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerocode.hotelPackagesApi.controller.request.CreateLoyaltyPointRequest;
import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
import com.zerocode.hotelPackagesApi.repository.LoyaltyPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoyaltyPointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoyaltyPointRepository loyaltyPointRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        loyaltyPointRepository.deleteAll();
        customerRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLoyaltyPoint_Success() throws Exception {
        Customer customer = new Customer();
        customer.setUserName("testuser");
        customer.setPassword("password");
        customer.setEmail("test@example.com");
        customer.setProfilePhotoUrl("http://example.com/photo.jpg");
        Customer savedCustomer = customerRepository.save(customer);

        CreateLoyaltyPointRequest req = new CreateLoyaltyPointRequest();
        req.setCount(100);
        req.setEarnedDate(LocalDate.now());

        mockMvc.perform(post("/customers/{customer_id}/loyalty-points", savedCustomer.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLoyaltyPoint_CustomerNotFound() throws Exception {
        CreateLoyaltyPointRequest req = new CreateLoyaltyPointRequest();
        req.setCount(100);
        req.setEarnedDate(LocalDate.now());

        mockMvc.perform(post("/customers/{customer_id}/loyalty-points", 99999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getLoyaltyPointById_Success() throws Exception {
        // Create customer first
        Customer customer = new Customer();
        customer.setUserName("testuser2");
        customer.setPassword("password");
        customer.setEmail("test2@example.com");
        customer.setProfilePhotoUrl("http://example.com/photo2.jpg");
        Customer savedCustomer = customerRepository.save(customer);

        // Create loyalty point
        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setCount(200);
        lp.setEarnedDate(LocalDate.now());
        LoyaltyPoint savedLp = loyaltyPointRepository.save(lp);

        // Associate loyalty point with customer
        savedCustomer.setLoyaltyPoint(savedLp);
        customerRepository.save(savedCustomer);

        mockMvc.perform(get("/loyalty-points/{customer_id}", savedCustomer.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLp.getId()))
                .andExpect(jsonPath("$.count").value(200));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getLoyaltyPointById_CustomerNotFound() throws Exception {
        mockMvc.perform(get("/loyalty-points/{customer_id}", 99999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLoyaltyPoint_Success() throws Exception {
        // Create customer first
        Customer customer = new Customer();
        customer.setUserName("testuser3");
        customer.setPassword("password");
        customer.setEmail("test3@example.com");
        customer.setProfilePhotoUrl("http://example.com/photo3.jpg");
        Customer savedCustomer = customerRepository.save(customer);

        // Create loyalty point
        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setCount(300);
        lp.setEarnedDate(LocalDate.now());
        LoyaltyPoint savedLp = loyaltyPointRepository.save(lp);

        // Associate loyalty point with customer
        savedCustomer.setLoyaltyPoint(savedLp);
        customerRepository.save(savedCustomer);

        LoyaltyPoint updateLp = new LoyaltyPoint();
        updateLp.setId(savedLp.getId());
        updateLp.setCount(400);
        updateLp.setEarnedDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/loyalty-points/{customer_id}", savedCustomer.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLp)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLoyaltyPoint_CustomerNotFound() throws Exception {
        LoyaltyPoint updateLp = new LoyaltyPoint();
        updateLp.setId(99999L);
        updateLp.setCount(400);
        updateLp.setEarnedDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/loyalty-points/{customer_id}", 99999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLp)))
                .andExpect(status().isNotFound());
    }
} 