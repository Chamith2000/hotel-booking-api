package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCustomer_Success() throws Exception {
        Customer customer = new Customer();
        customer.setUserName("john_doe");
        customer.setPassword("password123");
        customer.setEmail("john.doe@example.com");
        customer.setProfilePhotoUrl("http://example.com/john.jpg");

        mockMvc.perform(post("/customers")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCustomer_AlreadyExists() throws Exception {
        Customer customer = new Customer();
        customer.setUserName("jane_smith");
        customer.setPassword("password456");
        customer.setEmail("jane.smith@example.com");
        customer.setProfilePhotoUrl("http://example.com/jane.jpg");
        customerRepository.save(customer);

        Customer duplicate = new Customer();
        duplicate.setUserName("jane_smith");
        duplicate.setPassword("password456");
        duplicate.setEmail("jane.smith@example.com");
        duplicate.setProfilePhotoUrl("http://example.com/jane.jpg");

        mockMvc.perform(post("/customers")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    // @Test
    // @WithMockUser(roles = "CUSTOMER")
    // void getAllCustomers_Empty() throws Exception {
    //     mockMvc.perform(get("/customers")
    //             .header("X-Api-Version", "v1"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.customers", hasSize(0)));
    // }

    // @Test
    // @WithMockUser(roles = "CUSTOMER")
    // void getAllCustomers_NonEmpty() throws Exception {
    //     Customer c1 = new Customer();
    //     c1.setUserName("alice");
    //     c1.setPassword("pw1");
    //     c1.setEmail("alice@example.com");
    //     c1.setProfilePhotoUrl("http://example.com/alice.jpg");
    //     Customer c2 = new Customer();
    //     c2.setUserName("bob");
    //     c2.setPassword("pw2");
    //     c2.setEmail("bob@example.com");
    //     c2.setProfilePhotoUrl("http://example.com/bob.jpg");
    //     customerRepository.save(c1);
    //     customerRepository.save(c2);

    //     mockMvc.perform(get("/customers")
    //             .header("X-Api-Version", "v1"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.customers", hasSize(2)))
    //             .andExpect(jsonPath("$.customers[*].email", containsInAnyOrder("alice@example.com", "bob@example.com")));
    // }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCustomerById_Success() throws Exception {
        Customer customer = new Customer();
        customer.setUserName("charlie");
        customer.setPassword("pw3");
        customer.setEmail("charlie.brown@example.com");
        customer.setProfilePhotoUrl("http://example.com/charlie.jpg");
        Customer saved = customerRepository.save(customer);

        mockMvc.perform(get("/customers/{customer_id}", saved.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.email").value("charlie.brown@example.com"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/customers/{customer_id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCustomerById_InvalidId() throws Exception {
        mockMvc.perform(get("/customers/{customer_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCustomer_Success() throws Exception {
        Customer customer = new Customer();
        customer.setUserName("david");
        customer.setPassword("pw4");
        customer.setEmail("david.smith@example.com");
        customer.setProfilePhotoUrl("http://example.com/david.jpg");
        Customer saved = customerRepository.save(customer);

        Customer updated = new Customer();
        updated.setUserName("david");
        updated.setPassword("pw5");
        updated.setEmail("david.smith@example.com");
        updated.setProfilePhotoUrl("http://example.com/david2.jpg");

        mockMvc.perform(put("/customers/{customer_id}", saved.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCustomer_NotFound() throws Exception {
        Customer updated = new Customer();
        updated.setUserName("eve");
        updated.setPassword("pw6");
        updated.setEmail("eve.smith@example.com");
        updated.setProfilePhotoUrl("http://example.com/eve.jpg");

        mockMvc.perform(put("/customers/{customer_id}", 9999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCustomer_InvalidId() throws Exception {
        Customer updated = new Customer();
        updated.setUserName("eve");
        updated.setPassword("pw6");
        updated.setEmail("eve.smith@example.com");
        updated.setProfilePhotoUrl("http://example.com/eve.jpg");

        mockMvc.perform(put("/customers/{customer_id}", "invalid")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCustomer_Success() throws Exception {
        Customer customer = new Customer();
        customer.setUserName("frank");
        customer.setPassword("pw7");
        customer.setEmail("frank.miller@example.com");
        customer.setProfilePhotoUrl("http://example.com/frank.jpg");
        Customer saved = customerRepository.save(customer);

        mockMvc.perform(delete("/customers/{customer_id}", saved.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCustomer_NotFound() throws Exception {
        mockMvc.perform(delete("/customers/{customer_id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCustomer_InvalidId() throws Exception {
        mockMvc.perform(delete("/customers/{customer_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }
}
