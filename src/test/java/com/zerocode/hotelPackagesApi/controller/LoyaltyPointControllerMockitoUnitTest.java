package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerocode.hotelPackagesApi.controller.request.CreateLoyaltyPointRequest;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import com.zerocode.hotelPackagesApi.service.LoyaltyPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoyaltyPointControllerMockitoUnitTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LoyaltyPointService loyaltyPointService;

    @InjectMocks
    private LoyaltyPointController loyaltyPointController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(loyaltyPointController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createLoyaltyPoint_Success() throws Exception {
        doNothing().when(loyaltyPointService).addLoyaltyPoint(eq(1L), any(CreateLoyaltyPointRequest.class));
        CreateLoyaltyPointRequest req = new CreateLoyaltyPointRequest();
        req.setCount(100);
        req.setEarnedDate(LocalDate.now());
        mockMvc.perform(post("/customers/{customer_id}/loyalty-points", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void createLoyaltyPoint_CustomerNotFound() throws Exception {
        doThrow(new CustomerNotFoundException("Customer not found")).when(loyaltyPointService).addLoyaltyPoint(eq(1L), any(CreateLoyaltyPointRequest.class));
        CreateLoyaltyPointRequest req = new CreateLoyaltyPointRequest();
        req.setCount(100);
        req.setEarnedDate(LocalDate.now());
        mockMvc.perform(post("/customers/{customer_id}/loyalty-points", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLoyaltyPointById_Success() throws Exception {
        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setId(1L);
        lp.setCount(200);
        lp.setEarnedDate(LocalDate.now());
        when(loyaltyPointService.getLoyaltyPointById(1L)).thenReturn(lp);
        mockMvc.perform(get("/loyalty-points/{customer_id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.count").value(200));
    }

    @Test
    void getLoyaltyPointById_CustomerNotFound() throws Exception {
        when(loyaltyPointService.getLoyaltyPointById(1L)).thenThrow(new CustomerNotFoundException("Customer not found"));
        mockMvc.perform(get("/loyalty-points/{customer_id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateLoyaltyPoint_Success() throws Exception {
        doNothing().when(loyaltyPointService).updateLoyaltyPoint(eq(1L), any(LoyaltyPoint.class));
        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setId(1L);
        lp.setCount(300);
        lp.setEarnedDate(LocalDate.now());
        mockMvc.perform(put("/loyalty-points/{customer_id}", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lp)))
                .andExpect(status().isOk());
    }

    @Test
    void updateLoyaltyPoint_CustomerNotFound() throws Exception {
        doThrow(new CustomerNotFoundException("Customer not found")).when(loyaltyPointService).updateLoyaltyPoint(eq(1L), any(LoyaltyPoint.class));
        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setId(1L);
        lp.setCount(300);
        lp.setEarnedDate(LocalDate.now());
        mockMvc.perform(put("/loyalty-points/{customer_id}", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lp)))
                .andExpect(status().isNotFound());
    }
} 