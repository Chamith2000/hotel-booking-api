package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerocode.hotelPackagesApi.controller.request.CreateNotificationRequestDTO;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import com.zerocode.hotelPackagesApi.model.Role;
import com.zerocode.hotelPackagesApi.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class NotificationControllerMockitoUnitTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createNotification_Success() throws Exception {
        CreateNotificationRequestDTO req = new CreateNotificationRequestDTO(
                "Test message",
                LocalDateTime.now(),
                true,
                Role.ADMIN
        );
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        verify(notificationService, times(1)).createNotification(any(CreateNotificationRequestDTO.class));
    }

    @Test
    void createNotification_BlankMessage() throws Exception {
        CreateNotificationRequestDTO req = new CreateNotificationRequestDTO(
                "",
                LocalDateTime.now(),
                true,
                Role.ADMIN
        );
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    void createNotification_NullSentDateAndTime() throws Exception {
        CreateNotificationRequestDTO req = new CreateNotificationRequestDTO(
                "Test message",
                null,
                true,
                Role.ADMIN
        );
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    void createNotification_NullStatus() throws Exception {
        CreateNotificationRequestDTO req = new CreateNotificationRequestDTO(
                "Test message",
                LocalDateTime.now(),
                null,
                Role.ADMIN
        );
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    void createNotification_NullRole() throws Exception {
        CreateNotificationRequestDTO req = new CreateNotificationRequestDTO(
                "Test message",
                LocalDateTime.now(),
                true,
                null
        );
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        verify(notificationService, never()).createNotification(any());
    }
} 