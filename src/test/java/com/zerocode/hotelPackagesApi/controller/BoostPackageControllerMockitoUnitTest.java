package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageList;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageListResponse;
import com.zerocode.hotelPackagesApi.exception.BoostPackageNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.service.BoostPackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BoostPackageControllerMockitoUnitTest {

    @Mock
    private BoostPackageService boostPackageService;

    @InjectMocks
    private BoostPackageController boostPackageController;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Boost Package - Success")
    void testCreateBoostPackageSuccess() throws PackageNotFoundException {
        // Arrange
        Long packageId = 1L;
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));

        // Mock service behavior (no return value, just verify call)
        doNothing().when(boostPackageService).createBoostPackage(eq(packageId), any(CreateBoostPackageRequestDTO.class));

        // Act
        boostPackageController.create(packageId, requestDTO);

        // Assert
        verify(boostPackageService, times(1)).createBoostPackage(packageId, requestDTO);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Create Boost Package - Package Not Found")
    void testCreateBoostPackageNotFound() throws PackageNotFoundException {
        // Arrange
        Long packageId = 999L;
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));

        // Mock service to throw exception
        doThrow(new PackageNotFoundException("Package not found with ID: " + packageId))
                .when(boostPackageService).createBoostPackage(eq(packageId), any(CreateBoostPackageRequestDTO.class));

        // Act & Assert
        PackageNotFoundException exception = assertThrows(PackageNotFoundException.class, () ->
                boostPackageController.create(packageId, requestDTO));
        assertEquals("Package not found with ID: 999", exception.getMessage());
        verify(boostPackageService, times(1)).createBoostPackage(packageId, requestDTO);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Create Boost Package - Null Request")
    void testCreateBoostPackageNullRequest() throws PackageNotFoundException {
        // Arrange
        Long packageId = 1L;
        CreateBoostPackageRequestDTO requestDTO = null;

        // Mock service behavior
        doNothing().when(boostPackageService).createBoostPackage(eq(packageId), eq(null));

        // Act
        boostPackageController.create(packageId, requestDTO);

        // Assert
        verify(boostPackageService, times(1)).createBoostPackage(packageId, null);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Create Boost Package - Zero Package ID")
    void testCreateBoostPackageZeroPackageId() throws PackageNotFoundException {
        // Arrange
        Long packageId = 0L;
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));

        // Mock service behavior
        doNothing().when(boostPackageService).createBoostPackage(eq(packageId), any(CreateBoostPackageRequestDTO.class));

        // Act
        boostPackageController.create(packageId, requestDTO);

        // Assert
        verify(boostPackageService, times(1)).createBoostPackage(packageId, requestDTO);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get Boost Package By ID - Success")
    void testGetBoostPackageByIdSuccess() throws BoostPackageNotFoundException, PackageNotFoundException {
        // Arrange
        Long boostPackageId = 1L;
        BoostPackageList mockBoostPackage = new BoostPackageList();
        mockBoostPackage.setId(boostPackageId);
        mockBoostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        mockBoostPackage.setName("Test Package");
        mockBoostPackage.setPrice(100.0);
        mockBoostPackage.setDescription("Test Description");

        when(boostPackageService.getBoostPackageById(boostPackageId)).thenReturn(mockBoostPackage);

        // Act
        BoostPackageList result = boostPackageController.getBoostPackageById(boostPackageId);

        // Assert
        assertNotNull(result);
        assertEquals(boostPackageId, result.getId());
        assertEquals("Test Package", result.getName());
        assertEquals(100.0, result.getPrice());
        assertEquals("Test Description", result.getDescription());
        assertEquals(LocalDate.of(2025, 4, 10), result.getBoostedDate());
        verify(boostPackageService, times(1)).getBoostPackageById(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get Boost Package By ID - Not Found")
    void testGetBoostPackageByIdNotFound() throws BoostPackageNotFoundException, PackageNotFoundException {
        // Arrange
        Long boostPackageId = 999L;

        when(boostPackageService.getBoostPackageById(boostPackageId))
                .thenThrow(new BoostPackageNotFoundException("Boost package not found with ID: " + boostPackageId));

        // Act & Assert
        BoostPackageNotFoundException exception = assertThrows(BoostPackageNotFoundException.class, () ->
                boostPackageController.getBoostPackageById(boostPackageId));
        assertEquals("Boost package not found with ID: 999", exception.getMessage());
        verify(boostPackageService, times(1)).getBoostPackageById(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get Boost Package By ID - Package Not Found Exception")
    void testGetBoostPackageByIdPackageNotFound() throws BoostPackageNotFoundException, PackageNotFoundException {
        // Arrange
        Long boostPackageId = 999L;

        when(boostPackageService.getBoostPackageById(boostPackageId))
                .thenThrow(new PackageNotFoundException("Package not found with ID: " + boostPackageId));

        // Act & Assert
        PackageNotFoundException exception = assertThrows(PackageNotFoundException.class, () ->
                boostPackageController.getBoostPackageById(boostPackageId));
        assertEquals("Package not found with ID: 999", exception.getMessage());
        verify(boostPackageService, times(1)).getBoostPackageById(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get Boost Package By ID - Zero ID")
    void testGetBoostPackageByIdZeroId() throws BoostPackageNotFoundException, PackageNotFoundException {
        // Arrange
        Long boostPackageId = 0L;
        BoostPackageList mockBoostPackage = new BoostPackageList();
        mockBoostPackage.setId(0L);
        mockBoostPackage.setName("Zero ID Package");

        when(boostPackageService.getBoostPackageById(boostPackageId)).thenReturn(mockBoostPackage);

        // Act
        BoostPackageList result = boostPackageController.getBoostPackageById(boostPackageId);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getId());
        assertEquals("Zero ID Package", result.getName());
        verify(boostPackageService, times(1)).getBoostPackageById(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get All Boost Packages - Success")
    void testGetBoostPackagesSuccess() throws BoostPackageNotFoundException {
        // Arrange
        BoostPackageList boostPackage1 = new BoostPackageList();
        boostPackage1.setId(1L);
        boostPackage1.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage1.setName("Package 1");

        BoostPackageList boostPackage2 = new BoostPackageList();
        boostPackage2.setId(2L);
        boostPackage2.setBoostedDate(LocalDate.of(2025, 4, 11));
        boostPackage2.setName("Package 2");

        List<BoostPackageList> boostPackages = Arrays.asList(boostPackage1, boostPackage2);
        BoostPackageListResponse mockResponse = new BoostPackageListResponse(boostPackages);

        when(boostPackageService.getBoostPackages()).thenReturn(mockResponse);

        // Act
        ResponseEntity<BoostPackageListResponse> response = boostPackageController.getBoostPackages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getBoostPackageLists().size());
        assertEquals(1L, response.getBody().getBoostPackageLists().get(0).getId());
        assertEquals(2L, response.getBody().getBoostPackageLists().get(1).getId());
        verify(boostPackageService, times(1)).getBoostPackages();
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get All Boost Packages - Empty List")
    void testGetBoostPackagesEmptyList() throws BoostPackageNotFoundException {
        // Arrange
        BoostPackageListResponse mockResponse = new BoostPackageListResponse(Collections.emptyList());

        when(boostPackageService.getBoostPackages()).thenReturn(mockResponse);

        // Act
        ResponseEntity<BoostPackageListResponse> response = boostPackageController.getBoostPackages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getBoostPackageLists().size());
        verify(boostPackageService, times(1)).getBoostPackages();
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get All Boost Packages - No Boost Packages Found")
    void testGetBoostPackagesNotFound() throws BoostPackageNotFoundException {
        // Arrange
        when(boostPackageService.getBoostPackages())
                .thenThrow(new BoostPackageNotFoundException("No boost packages found"));

        // Act & Assert
        BoostPackageNotFoundException exception = assertThrows(BoostPackageNotFoundException.class, () ->
                boostPackageController.getBoostPackages());
        assertEquals("No boost packages found", exception.getMessage());
        verify(boostPackageService, times(1)).getBoostPackages();
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Delete Boost Package - Success")
    void testDeleteBoostPackageSuccess() throws BoostPackageNotFoundException {
        // Arrange
        Long boostPackageId = 1L;

        doNothing().when(boostPackageService).deleteBoostPackage(boostPackageId);

        // Act
        boostPackageController.deleteBoostPackage(boostPackageId);

        // Assert
        verify(boostPackageService, times(1)).deleteBoostPackage(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Delete Boost Package - Not Found")
    void testDeleteBoostPackageNotFound() throws BoostPackageNotFoundException {
        // Arrange
        Long boostPackageId = 999L;

        doThrow(new BoostPackageNotFoundException("Boost package not found with ID: " + boostPackageId))
                .when(boostPackageService).deleteBoostPackage(boostPackageId);

        // Act & Assert
        BoostPackageNotFoundException exception = assertThrows(BoostPackageNotFoundException.class, () ->
                boostPackageController.deleteBoostPackage(boostPackageId));
        assertEquals("Boost package not found with ID: 999", exception.getMessage());
        verify(boostPackageService, times(1)).deleteBoostPackage(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Delete Boost Package - Zero ID")
    void testDeleteBoostPackageZeroId() throws BoostPackageNotFoundException {
        // Arrange
        Long boostPackageId = 0L;

        doNothing().when(boostPackageService).deleteBoostPackage(boostPackageId);

        // Act
        boostPackageController.deleteBoostPackage(boostPackageId);

        // Assert
        verify(boostPackageService, times(1)).deleteBoostPackage(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Create Boost Package - Future Date")
    void testCreateBoostPackageFutureDate() throws PackageNotFoundException {
        // Arrange
        Long packageId = 1L;
        LocalDate futureDate = LocalDate.now().plusDays(30);
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(futureDate);

        // Mock service behavior
        doNothing().when(boostPackageService).createBoostPackage(eq(packageId), any(CreateBoostPackageRequestDTO.class));

        // Act
        boostPackageController.create(packageId, requestDTO);

        // Assert
        verify(boostPackageService, times(1)).createBoostPackage(packageId, requestDTO);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Create Boost Package - Past Date")
    void testCreateBoostPackagePastDate() throws PackageNotFoundException {
        // Arrange
        Long packageId = 1L;
        LocalDate pastDate = LocalDate.now().minusDays(30);
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(pastDate);

        // Mock service behavior
        doNothing().when(boostPackageService).createBoostPackage(eq(packageId), any(CreateBoostPackageRequestDTO.class));

        // Act
        boostPackageController.create(packageId, requestDTO);

        // Assert
        verify(boostPackageService, times(1)).createBoostPackage(packageId, requestDTO);
        verifyNoMoreInteractions(boostPackageService);
    }

    @Test
    @DisplayName("Get Boost Package By ID - Large ID")
    void testGetBoostPackageByIdLargeId() throws BoostPackageNotFoundException, PackageNotFoundException {
        // Arrange
        Long boostPackageId = Long.MAX_VALUE;
        BoostPackageList mockBoostPackage = new BoostPackageList();
        mockBoostPackage.setId(boostPackageId);
        mockBoostPackage.setName("Large ID Package");

        when(boostPackageService.getBoostPackageById(boostPackageId)).thenReturn(mockBoostPackage);

        // Act
        BoostPackageList result = boostPackageController.getBoostPackageById(boostPackageId);

        // Assert
        assertNotNull(result);
        assertEquals(Long.MAX_VALUE, result.getId());
        assertEquals("Large ID Package", result.getName());
        verify(boostPackageService, times(1)).getBoostPackageById(boostPackageId);
        verifyNoMoreInteractions(boostPackageService);
    }
}