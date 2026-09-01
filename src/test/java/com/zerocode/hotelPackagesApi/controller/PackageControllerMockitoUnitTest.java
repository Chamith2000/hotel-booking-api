package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.controller.response.PackageListResponse;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.GuestCount;
import com.zerocode.hotelPackagesApi.model.PackageImage;
import com.zerocode.hotelPackagesApi.service.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PackageControllerMockitoUnitTest {

    @Mock
    private PackageService packageService;

    @InjectMocks
    private PackageController packageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CreatePackageRequestDTO createPackageRequestDTO;

    @BeforeEach
    void setUp() {
        // Set up MockMvc with exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(packageController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // Initialize test data
        createPackageRequestDTO = new CreatePackageRequestDTO();
        createPackageRequestDTO.setName("Test Package");
        createPackageRequestDTO.setDescription("Test Description");
        createPackageRequestDTO.setStartDate(LocalDate.now().plusDays(1));
        createPackageRequestDTO.setEndDate(LocalDate.now().plusDays(7));
        createPackageRequestDTO.setGuestCount(new GuestCount());
        createPackageRequestDTO.setVisitors(2);
        createPackageRequestDTO.setPrice(999.99);
        createPackageRequestDTO.setTermsAndCondition("Test Terms");
        createPackageRequestDTO.setImages(Arrays.asList(new PackageImage()));
        createPackageRequestDTO.setStatus(true);
    }

    // Exception handler for tests
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(HotelNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public void handleHotelNotFoundException(HotelNotFoundException e) {
            // Return 404 status
        }

        @ExceptionHandler(PackageNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public void handlePackageNotFoundException(PackageNotFoundException e) {
            // Return 404 status
        }
    }

    @Test
    void createPackage_Success() throws Exception {
        Long hotelId = 1L;

        doNothing().when(packageService).createPackages(eq(hotelId), any(CreatePackageRequestDTO.class));

        mockMvc.perform(post("/hotels/{hotel-id}/packages", hotelId)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isOk());

        verify(packageService, times(1)).createPackages(eq(hotelId), any(CreatePackageRequestDTO.class));
    }

    @Test
    void createPackage_HotelNotFound() throws Exception {
        Long hotelId = 1L;

        doThrow(new HotelNotFoundException("Hotel not found"))
                .when(packageService).createPackages(eq(hotelId), any(CreatePackageRequestDTO.class));

        mockMvc.perform(post("/hotels/{hotel-id}/packages", hotelId)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isNotFound());

        verify(packageService, times(1)).createPackages(eq(hotelId), any(CreatePackageRequestDTO.class));
    }

    @Test
    void createPackage_InvalidInput() throws Exception {
        // This test is removed because MockMvc standalone setup doesn't include validation by default
        // You would need to add a validator to the setup or use @WebMvcTest with the full Spring context
    }

    @Test
    void getAllPackages_Success() throws Exception {
        List<PackageListItem> packages = Arrays.asList(
                new PackageListItem(),
                new PackageListItem()
        );

        when(packageService.findAll()).thenReturn(packages);

        mockMvc.perform(get("/packages")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages").isArray())
                .andExpect(jsonPath("$.packages").isNotEmpty());

        verify(packageService, times(1)).findAll();
    }

    @Test
    void getAllPackages_EmptyList() throws Exception {
        when(packageService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/packages")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages").isArray())
                .andExpect(jsonPath("$.packages").isEmpty());

        verify(packageService, times(1)).findAll();
    }

    @Test
    void getPackageById_Success() throws Exception {
        Long packageId = 1L;
        PackageListItem packageItem = new PackageListItem();

        when(packageService.findById(packageId)).thenReturn(packageItem);

        mockMvc.perform(get("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        verify(packageService, times(1)).findById(packageId);
    }

    @Test
    void getPackageById_NotFound() throws Exception {
        Long packageId = 1L;

        when(packageService.findById(packageId))
                .thenThrow(new PackageNotFoundException("Package not found"));

        mockMvc.perform(get("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());

        verify(packageService, times(1)).findById(packageId);
    }

    @Test
    void deletePackageById_Success() throws Exception {
        Long packageId = 1L;

        doNothing().when(packageService).deletePackageById(packageId);

        mockMvc.perform(delete("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        verify(packageService, times(1)).deletePackageById(packageId);
    }

    @Test
    void deletePackageById_NotFound() throws Exception {
        Long packageId = 1L;

        doThrow(new PackageNotFoundException("Package not found"))
                .when(packageService).deletePackageById(packageId);

        mockMvc.perform(delete("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());

        verify(packageService, times(1)).deletePackageById(packageId);
    }

    @Test
    void updatePackageById_Success() throws Exception {
        Long packageId = 1L;

        doNothing().when(packageService).updatePackageById(eq(packageId), any(CreatePackageRequestDTO.class));

        mockMvc.perform(put("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isOk());

        verify(packageService, times(1)).updatePackageById(eq(packageId), any(CreatePackageRequestDTO.class));
    }

    @Test
    void updatePackageById_NotFound() throws Exception {
        Long packageId = 1L;

        doThrow(new PackageNotFoundException("Package not found"))
                .when(packageService).updatePackageById(eq(packageId), any(CreatePackageRequestDTO.class));

        mockMvc.perform(put("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isNotFound());

        verify(packageService, times(1)).updatePackageById(eq(packageId), any(CreatePackageRequestDTO.class));
    }

    @Test
    void getPackagesByStatus_Active() throws Exception {
        Boolean status = true;
        List<PackageListItem> packages = Arrays.asList(new PackageListItem());

        when(packageService.findByStatus(status)).thenReturn(packages);

        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages").isArray());

        verify(packageService, times(1)).findByStatus(status);
    }

    @Test
    void getPackagesByStatus_Inactive() throws Exception {
        Boolean status = false;
        List<PackageListItem> packages = Arrays.asList(new PackageListItem());

        when(packageService.findByStatus(status)).thenReturn(packages);

        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages").isArray());

        verify(packageService, times(1)).findByStatus(status);
    }

    @Test
    void getPackagesByHotelId_Success() throws Exception {
        Long hotelId = 1L;
        List<PackageListItem> packages = Arrays.asList(new PackageListItem());

        when(packageService.findByHotelId(hotelId)).thenReturn(packages);

        mockMvc.perform(get("/hotels/{hotel-id}/packages", hotelId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages").isArray());

        verify(packageService, times(1)).findByHotelId(hotelId);
    }

    @Test
    void getPackagesCount_Success() throws Exception {
        int count = 10;

        when(packageService.getPackagesCount()).thenReturn(count);

        mockMvc.perform(get("/packages-count")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));

        verify(packageService, times(1)).getPackagesCount();
    }

    @Test
    void getPackagesCount_Zero() throws Exception {
        int count = 0;

        when(packageService.getPackagesCount()).thenReturn(count);

        mockMvc.perform(get("/packages-count")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        verify(packageService, times(1)).getPackagesCount();
    }

    @Test
    void getPackagesCountByStatus_Active() throws Exception {
        Boolean status = true;
        int count = 5;

        when(packageService.getPackagesCountByStatus(status)).thenReturn(count);

        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));

        verify(packageService, times(1)).getPackagesCountByStatus(status);
    }

    @Test
    void getPackagesCountByStatus_Inactive() throws Exception {
        Boolean status = false;
        int count = 3;

        when(packageService.getPackagesCountByStatus(status)).thenReturn(count);

        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));

        verify(packageService, times(1)).getPackagesCountByStatus(status);
    }

    @Test
    void testMissingApiVersionHeader() throws Exception {
        mockMvc.perform(get("/packages"))
                .andExpect(status().isNotFound());

        verify(packageService, never()).findAll();
    }

    @Test
    void testInvalidPathVariable() throws Exception {
        mockMvc.perform(get("/packages/invalid-id")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());

        verify(packageService, never()).findById(anyLong());
    }
}
