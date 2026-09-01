package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.model.PackageType;
import com.zerocode.hotelPackagesApi.repository.PackageTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PackageTypeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PackageTypeRepository packageTypeRepository;

    @BeforeEach
    void setUp() {
        packageTypeRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPackageType_Success() throws Exception {
        CreatePackageTypeRequest req = new CreatePackageTypeRequest("Premium");
        mockMvc.perform(post("/package-types")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPackageType_Duplicate() throws Exception {
        PackageType existing = new PackageType();
        existing.setName("Premium");
        packageTypeRepository.save(existing);
        CreatePackageTypeRequest req = new CreatePackageTypeRequest("Premium");
        mockMvc.perform(post("/package-types")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPackageType_Invalid() throws Exception {
        CreatePackageTypeRequest req = new CreatePackageTypeRequest("");
        mockMvc.perform(post("/package-types")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllPackageTypes_Empty() throws Exception {
        mockMvc.perform(get("/package-types")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageTypeLists", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllPackageTypes_NonEmpty() throws Exception {
        PackageType p1 = new PackageType();
        p1.setName("Premium");
        PackageType p2 = new PackageType();
        p2.setName("Basic");
        packageTypeRepository.save(p1);
        packageTypeRepository.save(p2);
        mockMvc.perform(get("/package-types")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageTypeLists", hasSize(2)))
                .andExpect(jsonPath("$.packageTypeLists[*].name", containsInAnyOrder("Premium", "Basic")));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackageTypeById_Success() throws Exception {
        PackageType p = new PackageType();
        p.setName("Premium");
        p = packageTypeRepository.save(p);
        mockMvc.perform(get("/package-types/{id}", p.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(p.getId()))
                .andExpect(jsonPath("$.name").value("Premium"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackageTypeById_NotFound() throws Exception {
        mockMvc.perform(get("/package-types/{id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackageTypeById_InvalidId() throws Exception {
        mockMvc.perform(get("/package-types/{id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePackageType_Success() throws Exception {
        PackageType p = new PackageType();
        p.setName("Premium");
        p = packageTypeRepository.save(p);
        UpdatePackageTypeRequest req = new UpdatePackageTypeRequest("Updated");
        mockMvc.perform(put("/package-types/{id}", p.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePackageType_NotFound() throws Exception {
        UpdatePackageTypeRequest req = new UpdatePackageTypeRequest("Updated");
        mockMvc.perform(put("/package-types/{id}", 9999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePackageType_InvalidId() throws Exception {
        UpdatePackageTypeRequest req = new UpdatePackageTypeRequest("Updated");
        mockMvc.perform(put("/package-types/{id}", "invalid")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePackageType_Success() throws Exception {
        PackageType p = new PackageType();
        p.setName("Premium");
        p = packageTypeRepository.save(p);
        mockMvc.perform(delete("/package-types/{id}", p.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePackageType_NotFound() throws Exception {
        mockMvc.perform(delete("/package-types/{id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePackageType_InvalidId() throws Exception {
        mockMvc.perform(delete("/package-types/{id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }
} 