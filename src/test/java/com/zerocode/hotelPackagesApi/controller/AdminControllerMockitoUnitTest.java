package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateAdminRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateAdminRequest;
import com.zerocode.hotelPackagesApi.exception.NotFoundException;
import com.zerocode.hotelPackagesApi.service.impl.AdminServiceImpl;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerMockitoUnitTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AdminServiceImpl adminServiceImpl;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createAdmin_Success() throws Exception {
        doNothing().when(adminServiceImpl).createAdmin(any(CreateAdminRequest.class));
        CreateAdminRequest req = new CreateAdminRequest();
        req.setEmail("admin@example.com");
        req.setPassword("password");
        req.setProfilePhotoUrl("url");
        req.setUserName("admin");
        mockMvc.perform(post("/admins")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void createAdmin_NotFound() throws Exception {
        doThrow(new NotFoundException("Admin already exists")).when(adminServiceImpl).createAdmin(any(CreateAdminRequest.class));
        CreateAdminRequest req = new CreateAdminRequest();
        req.setEmail("admin@example.com");
        req.setPassword("password");
        req.setProfilePhotoUrl("url");
        req.setUserName("admin");
        mockMvc.perform(post("/admins")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAdmin_Success() throws Exception {
        doNothing().when(adminServiceImpl).updateAdmin(any(Long.class), any(UpdateAdminRequest.class));
        UpdateAdminRequest req = new UpdateAdminRequest();
        req.setEmail("admin@example.com");
        req.setProfilePhotoUrl("url");
        req.setUserName("admin");
        mockMvc.perform(put("/1")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAdmin_NotFound() throws Exception {
        doThrow(new NotFoundException("Admin not found")).when(adminServiceImpl).updateAdmin(any(Long.class), any(UpdateAdminRequest.class));
        UpdateAdminRequest req = new UpdateAdminRequest();
        req.setEmail("admin@example.com");
        req.setProfilePhotoUrl("url");
        req.setUserName("admin");
        mockMvc.perform(put("/1")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAdmin_Success() throws Exception {
        doNothing().when(adminServiceImpl).deleteAdmin(any(Long.class));
        mockMvc.perform(delete("/1")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAdmin_NotFound() throws Exception {
        doThrow(new NotFoundException("Admin not found")).when(adminServiceImpl).deleteAdmin(any(Long.class));
        mockMvc.perform(delete("/1")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }
} 