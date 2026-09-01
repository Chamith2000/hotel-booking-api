package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateReviewRequest;
import com.zerocode.hotelPackagesApi.controller.response.ReviewResponse;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.ReviewNotFoundException;
import com.zerocode.hotelPackagesApi.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerMockitoUnitTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    @WithMockUser
    void createReview_Success() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(5, "Great!", LocalDateTime.now());
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", 1L, 2L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        verify(reviewService, times(1)).createReview(eq(1L), eq(2L), any(CreateReviewRequest.class));
    }

    @Test
    @WithMockUser
    void createReview_CustomerNotFound() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(5, "Great!", LocalDateTime.now());
        doThrow(new CustomerNotFoundException("not found")).when(reviewService).createReview(eq(1L), eq(2L), any(CreateReviewRequest.class));
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", 1L, 2L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createReview_HotelNotFound() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest(5, "Great!", LocalDateTime.now());
        doThrow(new HotelNotFoundException("not found")).when(reviewService).createReview(eq(1L), eq(2L), any(CreateReviewRequest.class));
        mockMvc.perform(post("/customers/{customer-id}/hotels/{hotel-id}/reviews", 1L, 2L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getReviewById_Success() throws Exception {
        ReviewResponse resp = ReviewResponse.builder()
                .id(10L)
                .rating(4)
                .comment("Nice!")
                .postedDateAndTime(LocalDateTime.now())
                .build();
        when(reviewService.findById(10L)).thenReturn(resp);
        mockMvc.perform(get("/reviews/{review_id}", 10L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Nice!"));
    }

    @Test
    @WithMockUser
    void getReviewById_NotFound() throws Exception {
        when(reviewService.findById(99L)).thenThrow(new ReviewNotFoundException("not found"));
        mockMvc.perform(get("/reviews/{review_id}", 99L)
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