package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageList;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageListResponse;
import com.zerocode.hotelPackagesApi.exception.BoostPackageNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.BoostPackageRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@WithMockUser(roles = "HOTEL")
class BoostPackageServiceImplIntegrationTest {

    @Autowired
    private BoostPackageServiceImpl boostPackageService;

    @Autowired
    private BoostPackageRepository boostPackageRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private HotelPackage testHotelPackage;

    @BeforeEach
    void setup() {
        boostPackageRepository.deleteAll();
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.saveAndFlush(testHotel);

        testHotelPackage = new HotelPackage();
        testHotelPackage.setName("Test Package");
        testHotelPackage.setPrice(100.0);
        testHotelPackage.setDescription("A test package");
        testHotelPackage.setStartDate(LocalDate.of(2025, 4, 1));
        testHotelPackage.setEndDate(LocalDate.of(2025, 4, 10));
        testHotelPackage.setTermsAndCondition("No refunds");
        testHotelPackage.setVisitorCount(10);
        testHotelPackage.setStatus(true);
        testHotelPackage.setPackageStatus(PackageStatus.APPROVED);
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);
        testHotelPackage.setGuestCount(guestCount);
        testHotelPackage.setHotel(testHotel);
        testHotelPackage = packageRepository.saveAndFlush(testHotelPackage);
    }

    @Test
    @DisplayName("Create boost package - Success")
    void testCreateBoostPackageSuccess() throws Exception {
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));
        boostPackageService.createBoostPackage(testHotelPackage.getId(), requestDTO);

        List<BoostPackage> boostPackages = boostPackageRepository.findAll();
        assertEquals(1, boostPackages.size());
        BoostPackage created = boostPackages.get(0);
        assertEquals(LocalDate.of(2025, 4, 10), created.getBoostedDate());
        assertEquals(testHotelPackage.getId(), created.getHotelPackage().getId());
        assertEquals(testHotel.getId(), created.getHotel().getId());
    }

    @Test
    @DisplayName("Create boost package - Package Not Found")
    void testCreateBoostPackagePackageNotFound() {
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));
        PackageNotFoundException ex = assertThrows(PackageNotFoundException.class, () ->
                boostPackageService.createBoostPackage(999L, requestDTO));
        assertTrue(ex.getMessage().contains("Package not found with ID: 999"));
        assertEquals(0, boostPackageRepository.count());
    }

    @Test
    @DisplayName("Get boost package by ID - Success")
    void testGetBoostPackageByIdSuccess() throws Exception {
        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage.setHotelPackage(testHotelPackage);
        boostPackage.setHotel(testHotel);
        boostPackage = boostPackageRepository.saveAndFlush(boostPackage);

        BoostPackageList result = boostPackageService.getBoostPackageById(boostPackage.getId());
        assertNotNull(result);
        assertEquals(boostPackage.getId(), result.getId());
        assertEquals(testHotelPackage.getName(), result.getName());
        assertEquals(testHotelPackage.getPrice(), result.getPrice());
        assertEquals(LocalDate.of(2025, 4, 10), result.getBoostedDate());
        assertEquals(testHotelPackage.getGuestCount().getAdults(), result.getGuestAdults());
        assertEquals(testHotelPackage.getGuestCount().getChildren(), result.getGuestChildren());
    }

    @Test
    @DisplayName("Get boost package by ID - Not Found")
    void testGetBoostPackageByIdNotFound() {
        assertThrows(BoostPackageNotFoundException.class, () ->
                boostPackageService.getBoostPackageById(999L));
    }

    @Test
    @DisplayName("Delete boost package - Success")
    void testDeleteBoostPackageSuccess() throws Exception {
        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage.setHotelPackage(testHotelPackage);
        boostPackage.setHotel(testHotel);
        boostPackage = boostPackageRepository.saveAndFlush(boostPackage);

        boostPackageService.deleteBoostPackage(boostPackage.getId());
        assertTrue(boostPackageRepository.findById(boostPackage.getId()).isEmpty());
        // Also check that the hotelPackage's boostPackage is null
        HotelPackage updatedPackage = packageRepository.findById(testHotelPackage.getId()).get();
        assertNull(updatedPackage.getBoostPackage());
    }

    @Test
    @DisplayName("Delete boost package - Not Found")
    void testDeleteBoostPackageNotFound() {
        assertThrows(BoostPackageNotFoundException.class, () ->
                boostPackageService.deleteBoostPackage(999L));
    }

    @Test
    @DisplayName("Get all boost packages - Success")
    @WithMockUser(roles = "ADMIN")
    void testGetBoostPackagesSuccess() throws Exception {
        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage.setHotelPackage(testHotelPackage);
        boostPackage.setHotel(testHotel);
        boostPackage = boostPackageRepository.saveAndFlush(boostPackage);

        BoostPackageListResponse response = boostPackageService.getBoostPackages();
        assertNotNull(response);
        assertEquals(1, response.getBoostPackageLists().size());
        BoostPackageList result = response.getBoostPackageLists().get(0);
        assertEquals(boostPackage.getId(), result.getId());
        assertEquals(testHotelPackage.getName(), result.getName());
        assertEquals(LocalDate.of(2025, 4, 10), result.getBoostedDate());
    }

    @Test
    @DisplayName("Get all boost packages - No Boost Packages Found")
    void testGetBoostPackagesNotFound() {
        assertThrows(BoostPackageNotFoundException.class, () ->
                boostPackageService.getBoostPackages());
    }
}
