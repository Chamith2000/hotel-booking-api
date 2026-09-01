package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateReviewRequest;
import com.zerocode.hotelPackagesApi.controller.response.ReviewResponse;
import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.Review;
import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private HotelRepository hotelRepository;

    private Customer customer;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        customerRepository.deleteAll();
        hotelRepository.deleteAll();
        customer = new Customer();
        customer.setUserName("testuser");
        customer.setPassword("pw");
        customer.setEmail("test@example.com");
        customer = customerRepository.save(customer);
        hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setApprovalStatus(ApprovalStatus.PENDING);
        hotel = hotelRepository.save(hotel);
    }

    @Test
    @WithMockUser
    void createReview_Success() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(5, "Great!", LocalDateTime.now());
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", customer.getId(), hotel.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void createReview_CustomerNotFound() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(5, "Great!", LocalDateTime.now());
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", 9999L, hotel.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createReview_HotelNotFound() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(5, "Great!", LocalDateTime.now());
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", customer.getId(), 9999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createReview_InvalidInput() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(0, "", null); // invalid rating and missing date
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", customer.getId(), hotel.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getReviewById_Success() throws Exception {
        Review review = new Review();
        review.setRating(4);
        review.setComment("Nice!");
        review.setPostedDateAndTime(LocalDateTime.now());
        review.setCustomer(customer);
        review.setHotel(hotel);
        review = reviewRepository.save(review);
        mockMvc.perform(get("/reviews/{review_id}", review.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Nice!"));
    }

    @Test
    @WithMockUser
    void getReviewById_NotFound() throws Exception {
        mockMvc.perform(get("/reviews/{review_id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getReviewById_InvalidId() throws Exception {
        mockMvc.perform(get("/reviews/{review_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }
}
