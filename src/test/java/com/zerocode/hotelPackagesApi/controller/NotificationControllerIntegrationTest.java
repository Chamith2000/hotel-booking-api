package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerocode.hotelPackagesApi.controller.request.CreateNotificationRequestDTO;
import com.zerocode.hotelPackagesApi.model.Notification;
import com.zerocode.hotelPackagesApi.model.Role;
import com.zerocode.hotelPackagesApi.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createNotification_Success() throws Exception {
        CreateNotificationRequestDTO req = new CreateNotificationRequestDTO(
                "Integration test message",
                LocalDateTime.now(),
                true,
                Role.ADMIN
        );
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getMessage()).isEqualTo("Integration test message");
        assertThat(notifications.get(0).getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
        assertThat(notificationRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
        assertThat(notificationRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
        assertThat(notificationRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
        assertThat(notificationRepository.findAll()).isEmpty();
    }
} 