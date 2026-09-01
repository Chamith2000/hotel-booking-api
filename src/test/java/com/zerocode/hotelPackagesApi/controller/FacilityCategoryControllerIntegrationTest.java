package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.model.FacilityCategory;
import com.zerocode.hotelPackagesApi.repository.FacilityCategoryRepository;
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
class FacilityCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacilityCategoryRepository facilityCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        facilityCategoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_Success() throws Exception {
        FacilityCategory facility = new FacilityCategory();
        facility.setCategoryName("Swimming Pool");

        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facility)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_AlreadyExists() throws Exception {
        FacilityCategory facility = new FacilityCategory();
        facility.setCategoryName("Gym");
        facilityCategoryRepository.save(facility);

        FacilityCategory duplicate = new FacilityCategory();
        duplicate.setCategoryName("Gym");

        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllFacilities_Empty() throws Exception {
        mockMvc.perform(get("/facilities")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllFacilities_NonEmpty() throws Exception {
        FacilityCategory f1 = new FacilityCategory();
        f1.setCategoryName("Spa");
        FacilityCategory f2 = new FacilityCategory();
        f2.setCategoryName("Gym");
        facilityCategoryRepository.save(f1);
        facilityCategoryRepository.save(f2);

        mockMvc.perform(get("/facilities")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities", hasSize(2)))
                .andExpect(jsonPath("$.facilities[*].category", containsInAnyOrder("Spa", "Gym")));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_Success() throws Exception {
        FacilityCategory facility = new FacilityCategory();
        facility.setCategoryName("Pool");
        FacilityCategory saved = facilityCategoryRepository.save(facility);

        mockMvc.perform(get("/facilities/{facility_id}", saved.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.categoryName").value("Pool"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_NotFound() throws Exception {
        mockMvc.perform(get("/facilities/{facility_id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_InvalidId() throws Exception {
        mockMvc.perform(get("/facilities/{facility_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_Success() throws Exception {
        FacilityCategory facility = new FacilityCategory();
        facility.setCategoryName("Old Name");
        FacilityCategory saved = facilityCategoryRepository.save(facility);

        FacilityCategory updated = new FacilityCategory();
        updated.setCategoryName("New Name");

        mockMvc.perform(put("/facilities/{facility_id}", saved.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_NotFound() throws Exception {
        FacilityCategory updated = new FacilityCategory();
        updated.setCategoryName("New Name");

        mockMvc.perform(put("/facilities/{facility_id}", 9999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_InvalidId() throws Exception {
        FacilityCategory updated = new FacilityCategory();
        updated.setCategoryName("New Name");

        mockMvc.perform(put("/facilities/{facility_id}", "invalid")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_Success() throws Exception {
        FacilityCategory f1 = new FacilityCategory();
        f1.setCategoryName("Spa");
        FacilityCategory f2 = new FacilityCategory();
        f2.setCategoryName("Gym");
        facilityCategoryRepository.save(f1);
        facilityCategoryRepository.save(f2);

        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", "Spa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities", hasSize(1)))
                .andExpect(jsonPath("$.facilities[0].category", is("Spa")));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_NotFound() throws Exception {
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", "NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_EmptyName() throws Exception {
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_Success() throws Exception {
        FacilityCategory facility = new FacilityCategory();
        facility.setCategoryName("DeleteMe");
        FacilityCategory saved = facilityCategoryRepository.save(facility);

        mockMvc.perform(delete("/facilities/{facility_id}", saved.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_NotFound() throws Exception {
        mockMvc.perform(delete("/facilities/{facility_id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_InvalidId() throws Exception {
        mockMvc.perform(delete("/facilities/{facility_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }
} 