package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateAdminRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateAdminRequest;
import com.zerocode.hotelPackagesApi.model.Admin;
import com.zerocode.hotelPackagesApi.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        adminRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAdmin_Success() throws Exception {
        CreateAdminRequest req = new CreateAdminRequest();
        req.setUserName("admin1");
        req.setPassword("password1");
        req.setEmail("admin1@example.com");
        req.setProfilePhotoUrl("http://example.com/admin1.jpg");

        mockMvc.perform(post("/admins")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAdmin_Duplicate() throws Exception {
        Admin admin = new Admin();
        admin.setUserName("admin2");
        admin.setPassword("password2");
        admin.setEmail("admin2@example.com");
        admin.setProfilePhotoUrl("http://example.com/admin2.jpg");
        adminRepository.save(admin);

        CreateAdminRequest req = new CreateAdminRequest();
        req.setUserName("admin2");
        req.setPassword("password2");
        req.setEmail("admin2@example.com");
        req.setProfilePhotoUrl("http://example.com/admin2.jpg");

        mockMvc.perform(post("/admins")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound()); // NotFoundException is thrown for duplicate
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdmin_Success() throws Exception {
        Admin admin = new Admin();
        admin.setUserName("admin3");
        admin.setPassword("password3");
        admin.setEmail("admin3@example.com");
        admin.setProfilePhotoUrl("http://example.com/admin3.jpg");
        Admin saved = adminRepository.save(admin);

        UpdateAdminRequest req = new UpdateAdminRequest();
        req.setUserName("admin3_updated");
        req.setEmail("admin3_updated@example.com");
        req.setProfilePhotoUrl("http://example.com/admin3_updated.jpg");

        mockMvc.perform(put("/" + saved.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdmin_NotFound() throws Exception {
        UpdateAdminRequest req = new UpdateAdminRequest();
        req.setUserName("notfound");
        req.setEmail("notfound@example.com");
        req.setProfilePhotoUrl("http://example.com/notfound.jpg");

        mockMvc.perform(put("/99999")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdmin_Success() throws Exception {
        Admin admin = new Admin();
        admin.setUserName("admin4");
        admin.setPassword("password4");
        admin.setEmail("admin4@example.com");
        admin.setProfilePhotoUrl("http://example.com/admin4.jpg");
        Admin saved = adminRepository.save(admin);

        mockMvc.perform(delete("/" + saved.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdmin_NotFound() throws Exception {
        mockMvc.perform(delete("/99999")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }
} 