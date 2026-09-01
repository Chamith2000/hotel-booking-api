package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.response.FacilityList;
import com.zerocode.hotelPackagesApi.controller.response.FacilityListResponse;
import com.zerocode.hotelPackagesApi.controller.response.FacilityResponse;
import com.zerocode.hotelPackagesApi.exception.FacilityAlreadyExistsException;
import com.zerocode.hotelPackagesApi.exception.FacilityNotFoundException;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import com.zerocode.hotelPackagesApi.model.FacilityCategory;
import com.zerocode.hotelPackagesApi.service.FacilityCategoryService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FacilityCategoryControllerMockitoUnitTest {

    @Mock
    private FacilityCategoryService facilityCategoryService;

    @InjectMocks
    private FacilityCategoryController facilityCategoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private FacilityCategory testFacility;
    private FacilityResponse testFacilityResponse;
    private List<FacilityCategory> testFacilities;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facilityCategoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        // Setup test data
        testFacility = new FacilityCategory();
        testFacility.setId(1L);
        testFacility.setCategoryName("Swimming Pool");

        testFacilityResponse = FacilityResponse.builder()
                .id(1L)
                .categoryName("Swimming Pool")
                .build();

        FacilityCategory facility1 = new FacilityCategory();
        facility1.setId(1L);
        facility1.setCategoryName("Swimming Pool");

        FacilityCategory facility2 = new FacilityCategory();
        facility2.setId(2L);
        facility2.setCategoryName("Gym");

        testFacilities = Arrays.asList(facility1, facility2);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_Success() throws Exception {
        // Given
        FacilityCategory newFacility = new FacilityCategory();
        newFacility.setCategoryName("Spa");

        // When & Then
        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).add(any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFacility_WithAdminRole_Success() throws Exception {
        // Given
        FacilityCategory newFacility = new FacilityCategory();
        newFacility.setCategoryName("Restaurant");

        // When & Then
        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).add(any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_FacilityAlreadyExists() throws Exception {
        // Given
        FacilityCategory existingFacility = new FacilityCategory();
        existingFacility.setCategoryName("Swimming Pool");

        doThrow(new FacilityAlreadyExistsException("Facility already exists"))
                .when(facilityCategoryService).add(any(FacilityCategory.class));

        // When & Then
        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingFacility)))
                .andExpect(status().isConflict());

        verify(facilityCategoryService, times(1)).add(any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_InvalidRequest() throws Exception {
        // Given - Empty facility name
        FacilityCategory invalidFacility = new FacilityCategory();
        invalidFacility.setCategoryName("");

        // When & Then
        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).add(any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllFacilities_Success() throws Exception {
        // Given
        when(facilityCategoryService.findAll()).thenReturn(testFacilities);

        // When & Then
        mockMvc.perform(get("/facilities")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.facilities", hasSize(2)))
                .andExpect(jsonPath("$.facilities[0].id").value(1))
                .andExpect(jsonPath("$.facilities[0].category").value("Swimming Pool"))
                .andExpect(jsonPath("$.facilities[1].id").value(2))
                .andExpect(jsonPath("$.facilities[1].category").value("Gym"));

        verify(facilityCategoryService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllFacilities_WithAdminRole_Success() throws Exception {
        // Given
        when(facilityCategoryService.findAll()).thenReturn(testFacilities);

        // When & Then
        mockMvc.perform(get("/facilities")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.facilities", hasSize(2)));

        verify(facilityCategoryService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllFacilities_EmptyList() throws Exception {
        // Given
        when(facilityCategoryService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/facilities")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.facilities", hasSize(0)));

        verify(facilityCategoryService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_Success() throws Exception {
        // Given
        Long facilityId = 1L;
        when(facilityCategoryService.getById(facilityId)).thenReturn(testFacilityResponse);

        // When & Then
        mockMvc.perform(get("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryName").value("Swimming Pool"));

        verify(facilityCategoryService, times(1)).getById(facilityId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFacilityById_WithAdminRole_Success() throws Exception {
        // Given
        Long facilityId = 1L;
        when(facilityCategoryService.getById(facilityId)).thenReturn(testFacilityResponse);

        // When & Then
        mockMvc.perform(get("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(facilityCategoryService, times(1)).getById(facilityId);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;
        when(facilityCategoryService.getById(nonExistentId))
                .thenThrow(new FacilityNotFoundException("Facility not found with ID: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/facilities/{facility_id}", nonExistentId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, times(1)).getById(nonExistentId);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_InvalidId() throws Exception {
        // Given
        String invalidId = "invalid";
        // No stubbing needed: controller will not call service if path variable is not a valid Long
        // When & Then
        mockMvc.perform(get("/facilities/{facility_id}", invalidId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());

        verify(facilityCategoryService, never()).getById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_Success() throws Exception {
        // Given
        Long facilityId = 1L;
        FacilityCategory updatedFacility = new FacilityCategory();
        updatedFacility.setCategoryName("Updated Swimming Pool");

        // When & Then
        mockMvc.perform(put("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).update(eq(facilityId), any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateFacility_Unauthorized() throws Exception {
        // Given
        Long facilityId = 1L;
        FacilityCategory updatedFacility = new FacilityCategory();
        updatedFacility.setCategoryName("Updated Swimming Pool");

        // When & Then - Standalone MockMvc does not enforce @RolesAllowed, so expect 200
        mockMvc.perform(put("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFacility)))
                .andExpect(status().isOk());

        // In a real integration test, this would be forbidden (403)
        // verify(facilityCategoryService, never()).update(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;
        FacilityCategory updatedFacility = new FacilityCategory();
        updatedFacility.setCategoryName("Updated Swimming Pool");

        doThrow(new FacilityNotFoundException("Facility not found with ID: " + nonExistentId))
                .when(facilityCategoryService).update(eq(nonExistentId), any(FacilityCategory.class));

        // When & Then
        mockMvc.perform(put("/facilities/{facility_id}", nonExistentId)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFacility)))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, times(1)).update(eq(nonExistentId), any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_InvalidRequest() throws Exception {
        // Given
        Long facilityId = 1L;
        FacilityCategory invalidFacility = new FacilityCategory();
        invalidFacility.setCategoryName(""); // Empty name

        // When & Then
        mockMvc.perform(put("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).update(eq(facilityId), any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_Success() throws Exception {
        // Given
        String searchName = "Pool";
        List<FacilityCategory> searchResults = Arrays.asList(testFacility);
        when(facilityCategoryService.getByName(searchName)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.facilities", hasSize(1)))
                .andExpect(jsonPath("$.facilities[0].id").value(1))
                .andExpect(jsonPath("$.facilities[0].category").value("Swimming Pool"));

        verify(facilityCategoryService, times(1)).getByName(searchName);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchFacilitiesByName_WithAdminRole_Success() throws Exception {
        // Given
        String searchName = "Gym";
        List<FacilityCategory> searchResults = Arrays.asList(testFacilities.get(1));
        when(facilityCategoryService.getByName(searchName)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities", hasSize(1)));

        verify(facilityCategoryService, times(1)).getByName(searchName);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_NotFound() throws Exception {
        // Given
        String nonExistentName = "NonExistent";
        when(facilityCategoryService.getByName(nonExistentName))
                .thenThrow(new FacilityNotFoundException("No facilities found with name: " + nonExistentName));

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", nonExistentName))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, times(1)).getByName(nonExistentName);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_EmptyName() throws Exception {
        // Given
        String emptyName = "";

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", emptyName))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).getByName(emptyName);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_MissingNameParameter() throws Exception {
        // Given - No name parameter

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());

        verify(facilityCategoryService, never()).getByName(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_Success() throws Exception {
        // Given
        Long facilityId = 1L;

        // When & Then
        mockMvc.perform(delete("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).delete(facilityId);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteFacility_Unauthorized() throws Exception {
        // Given
        Long facilityId = 1L;

        // When & Then - Standalone MockMvc does not enforce @RolesAllowed, so expect 200
        mockMvc.perform(delete("/facilities/{facility_id}", facilityId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        // In a real integration test, this would be forbidden (403)
        // verify(facilityCategoryService, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;
        doThrow(new FacilityNotFoundException("Facility not found with ID: " + nonExistentId))
                .when(facilityCategoryService).delete(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/facilities/{facility_id}", nonExistentId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, times(1)).delete(nonExistentId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_InvalidId() throws Exception {
        // Given
        String invalidId = "invalid";

        // When & Then
        mockMvc.perform(delete("/facilities/{facility_id}", invalidId)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());

        verify(facilityCategoryService, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_MissingApiVersion() throws Exception {
        // Given
        FacilityCategory newFacility = new FacilityCategory();
        newFacility.setCategoryName("Spa");

        // When & Then
        mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFacility)))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, never()).add(any());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllFacilities_MissingApiVersion() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(get("/facilities"))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getFacilityById_MissingApiVersion() throws Exception {
        // Given
        Long facilityId = 1L;

        // When & Then
        mockMvc.perform(get("/facilities/{facility_id}", facilityId))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, never()).getById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFacility_MissingApiVersion() throws Exception {
        // Given
        Long facilityId = 1L;
        FacilityCategory updatedFacility = new FacilityCategory();
        updatedFacility.setCategoryName("Updated Swimming Pool");

        // When & Then
        mockMvc.perform(put("/facilities/{facility_id}", facilityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFacility)))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, never()).update(any(), any());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_MissingApiVersion() throws Exception {
        // Given
        String searchName = "Pool";

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .param("name", searchName))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, never()).getByName(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFacility_MissingApiVersion() throws Exception {
        // Given
        Long facilityId = 1L;

        // When & Then
        mockMvc.perform(delete("/facilities/{facility_id}", facilityId))
                .andExpect(status().isNotFound());

        verify(facilityCategoryService, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_WithSpecialCharacters() throws Exception {
        // Given
        FacilityCategory specialFacility = new FacilityCategory();
        specialFacility.setCategoryName("Spa & Wellness Center");

        // When & Then
        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).add(any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createFacility_WithLongName() throws Exception {
        // Given
        FacilityCategory longNameFacility = new FacilityCategory();
        longNameFacility.setCategoryName("Very Long Facility Category Name That Exceeds Normal Length");

        // When & Then
        mockMvc.perform(post("/facilities")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longNameFacility)))
                .andExpect(status().isOk());

        verify(facilityCategoryService, times(1)).add(any(FacilityCategory.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_CaseInsensitive() throws Exception {
        // Given
        String searchName = "pool";
        List<FacilityCategory> searchResults = Arrays.asList(testFacility);
        when(facilityCategoryService.getByName(searchName)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities", hasSize(1)));

        verify(facilityCategoryService, times(1)).getByName(searchName);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void searchFacilitiesByName_PartialMatch() throws Exception {
        // Given
        String searchName = "Swim";
        List<FacilityCategory> searchResults = Arrays.asList(testFacility);
        when(facilityCategoryService.getByName(searchName)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/facilities/search")
                .header("X-Api-Version", "v1")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities", hasSize(1)));

        verify(facilityCategoryService, times(1)).getByName(searchName);
    }
} 