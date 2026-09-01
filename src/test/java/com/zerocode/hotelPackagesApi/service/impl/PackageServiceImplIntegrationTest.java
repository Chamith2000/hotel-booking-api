package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PackageServiceImplIntegrationTest {

    @Autowired
    private PackageServiceImpl packageService;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;

    @BeforeEach
    void setup() {
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.saveAndFlush(testHotel);
    }

    private CreatePackageRequestDTO createPackageRequestDTO(String name, boolean status) {
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);

        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");

        CreatePackageRequestDTO dto = new CreatePackageRequestDTO();
        dto.setName(name);
        dto.setPrice(100.0);
        dto.setDescription("A test package");
        dto.setStartDate(LocalDate.of(2025, 3, 28));
        dto.setEndDate(LocalDate.of(2025, 4, 4));
        dto.setTermsAndCondition("No refunds");
        dto.setGuestCount(guestCount);
        dto.setVisitors(10);
        dto.setStatus(status);
        dto.setImages(new ArrayList<>(List.of(image)));
        return dto;
    }

    private HotelPackage createTestPackage(boolean status, PackageStatus packageStatus) {
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);

        HotelPackage hotelPackage = new HotelPackage();
        hotelPackage.setName("Test Package");
        hotelPackage.setPrice(100.0);
        hotelPackage.setDescription("A test package");
        hotelPackage.setStartDate(LocalDate.now());
        hotelPackage.setEndDate(LocalDate.now().plusDays(7));
        hotelPackage.setTermsAndCondition("No refunds");
        hotelPackage.setVisitorCount(0);
        hotelPackage.setStatus(status);
        hotelPackage.setPackageStatus(packageStatus);
        hotelPackage.setGuestCount(guestCount);
        hotelPackage.setHotel(testHotel);

        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        image.setHotelPackage(hotelPackage);
        hotelPackage.setPackageImages(new ArrayList<>(List.of(image)));

        return hotelPackage;
    }

    @Test
    @DisplayName("Create package - Success")
    void testCreatePackageSuccess() throws HotelNotFoundException {
        CreatePackageRequestDTO requestDTO = createPackageRequestDTO("Test Package", true);
        packageService.createPackages(testHotel.getId(), requestDTO);

        List<HotelPackage> packages = packageRepository.findAll();
        assertEquals(1, packages.size());
        HotelPackage createdPackage = packages.get(0);
        assertEquals("Test Package", createdPackage.getName());
        assertEquals(100.0, createdPackage.getPrice());
        assertEquals(testHotel.getId(), createdPackage.getHotel().getId());
        assertEquals(1, createdPackage.getPackageImages().size());
        assertEquals("http://example.com/image.jpg", createdPackage.getPackageImages().get(0).getUrl());
    }

    @Test
    @DisplayName("Create package - Hotel not found")
    void testCreatePackageHotelNotFound() {
        CreatePackageRequestDTO requestDTO = createPackageRequestDTO("Test Package", true);
        HotelNotFoundException exception = assertThrows(HotelNotFoundException.class, () ->
                packageService.createPackages(999L, requestDTO)
        );
        assertEquals("Hotel not found with ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Delete package - Success")
    void testDeletePackageSuccess() throws PackageNotFoundException {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        package1 = packageRepository.save(package1);

        packageService.deletePackageById(package1.getId());
        assertTrue(packageRepository.findById(package1.getId()).isEmpty());
    }

    @Test
    @DisplayName("Delete package - Package not found")
    void testDeletePackageNotFound() {
        PackageNotFoundException exception = assertThrows(PackageNotFoundException.class, () ->
                packageService.deletePackageById(999L)
        );
        assertEquals("Package not found with ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Update package - Success")
    void testUpdatePackageSuccess() throws PackageNotFoundException {
        // Arrange: Create and save an initial package
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        package1 = packageRepository.save(package1);

        // Prepare updated DTO
        GuestCount updatedGuestCount = new GuestCount();
        updatedGuestCount.setAdults(3);
        updatedGuestCount.setChildren(2);

        PackageImage updatedImage = new PackageImage();
        updatedImage.setUrl("http://example.com/updated.jpg");

        CreatePackageRequestDTO updatedDTO = new CreatePackageRequestDTO();
        updatedDTO.setName("Updated Package");
        updatedDTO.setPrice(150.0);
        updatedDTO.setDescription("Updated description");
        updatedDTO.setStartDate(LocalDate.of(2025, 4, 1));
        updatedDTO.setEndDate(LocalDate.of(2025, 4, 8));
        updatedDTO.setTermsAndCondition("Updated terms");
        updatedDTO.setGuestCount(updatedGuestCount);
        updatedDTO.setVisitors(50);
        updatedDTO.setStatus(false);
        updatedDTO.setImages(new ArrayList<>(List.of(updatedImage)));

        // Act: Update the package
        packageService.updatePackageById(package1.getId(), updatedDTO);

        // Assert: Verify the updated package
        HotelPackage updatedPackage = packageRepository.findById(package1.getId()).get();
        assertEquals("Updated Package", updatedPackage.getName());
        assertEquals(150.0, updatedPackage.getPrice());
        assertEquals("Updated description", updatedPackage.getDescription());
        assertFalse(updatedPackage.getStatus());
        assertEquals(3, updatedPackage.getGuestCount().getAdults());
        assertEquals(1, updatedPackage.getPackageImages().size());
        assertEquals("http://example.com/updated.jpg", updatedPackage.getPackageImages().get(0).getUrl());
    }

    @Test
    @DisplayName("Update package - Not found")
    void testUpdatePackageNotFound() {
        CreatePackageRequestDTO updatedDTO = createPackageRequestDTO("Updated Package", true);
        PackageNotFoundException exception = assertThrows(PackageNotFoundException.class, () ->
                packageService.updatePackageById(999L, updatedDTO)
        );
        assertEquals("Package not found with ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Find all packages - Success")
    void testFindAllPackagesSuccess() {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        packageRepository.save(package1);

        var packages = packageService.findAll();
        assertEquals(1, packages.size());
        assertEquals("Test Package", packages.get(0).getName());
    }

    @Test
    @DisplayName("Find package by ID - Success")
    void testFindByIdSuccess() throws PackageNotFoundException {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        package1 = packageRepository.save(package1);

        var packageItem = packageService.findById(package1.getId());
        assertEquals("Test Package", packageItem.getName());
    }

    @Test
    @DisplayName("Find packages by hotel ID - Success")
    void testFindByHotelId() {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        packageRepository.save(package1);

        var packages = packageService.findByHotelId(testHotel.getId());
        assertEquals(1, packages.size());
        assertEquals("Test Package", packages.get(0).getName());
    }

    @Test
    @DisplayName("Find packages by status - Success")
    void testFindByStatus() {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        HotelPackage package2 = createTestPackage(false, PackageStatus.REJECTED);
        packageRepository.save(package1);
        packageRepository.save(package2);

        var activePackages = packageService.findByStatus(true);
        assertEquals(1, activePackages.size());
        assertEquals("Test Package", activePackages.get(0).getName());
    }

    @Test
    @DisplayName("Get packages count - Success")
    void testGetPackagesCount() {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        packageRepository.save(package1);

        int count = packageService.getPackagesCount();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Get packages count by status - Success")
    void testGetPackagesCountByStatus() {
        HotelPackage package1 = createTestPackage(true, PackageStatus.APPROVED);
        HotelPackage package2 = createTestPackage(false, PackageStatus.REJECTED);
        packageRepository.save(package1);
        packageRepository.save(package2);

        int count = packageService.getPackagesCountByStatus(true);
        assertEquals(1, count);
    }
}
