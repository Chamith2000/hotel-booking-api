package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeList;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeListResponse;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotFoundException;
import com.zerocode.hotelPackagesApi.service.PackageTypeService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PackageTypeControllerUnitTest {

    @Mock
    private PackageTypeService packageTypeService;

    @InjectMocks
    private PackageTypeController packageTypeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(packageTypeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
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
        verify(packageTypeService, times(1)).create(any(CreatePackageTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPackageType_NotCreated() throws Exception {
        CreatePackageTypeRequest req = new CreatePackageTypeRequest("Premium");
        doThrow(new PackageTypeNotCreatedException("not created")).when(packageTypeService).create(any(CreatePackageTypeRequest.class));
        mockMvc.perform(post("/package-types")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllPackageTypes_Empty() throws Exception {
        when(packageTypeService.getAll()).thenReturn(new PackageTypeListResponse(Collections.emptyList()));
        mockMvc.perform(get("/package-types")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageTypeLists", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllPackageTypes_NonEmpty() throws Exception {
        PackageTypeList p1 = new PackageTypeList(1L, "Premium");
        PackageTypeList p2 = new PackageTypeList(2L, "Basic");
        when(packageTypeService.getAll()).thenReturn(new PackageTypeListResponse(Arrays.asList(p1, p2)));
        mockMvc.perform(get("/package-types")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageTypeLists", hasSize(2)))
                .andExpect(jsonPath("$.packageTypeLists[*].name", containsInAnyOrder("Premium", "Basic")));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackageTypeById_Success() throws Exception {
        PackageTypeList p = new PackageTypeList(1L, "Premium");
        when(packageTypeService.getById(1L)).thenReturn(p);
        mockMvc.perform(get("/package-types/{id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Premium"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackageTypeById_NotFound() throws Exception {
        when(packageTypeService.getById(99L)).thenThrow(new PackageTypeNotFoundException("not found"));
        mockMvc.perform(get("/package-types/{id}", 99L)
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
        UpdatePackageTypeRequest req = new UpdatePackageTypeRequest("Updated");
        mockMvc.perform(put("/package-types/{id}", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        verify(packageTypeService, times(1)).update(eq(1L), any(UpdatePackageTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePackageType_NotFound() throws Exception {
        UpdatePackageTypeRequest req = new UpdatePackageTypeRequest("Updated");
        doThrow(new PackageTypeNotFoundException("not found")).when(packageTypeService).update(eq(99L), any(UpdatePackageTypeRequest.class));
        mockMvc.perform(put("/package-types/{id}", 99L)
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
        mockMvc.perform(delete("/package-types/{id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
        verify(packageTypeService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePackageType_NotFound() throws Exception {
        doThrow(new PackageTypeNotFoundException("not found")).when(packageTypeService).delete(99L);
        mockMvc.perform(delete("/package-types/{id}", 99L)
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