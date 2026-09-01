package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.BoostPackage;
import com.zerocode.hotelPackagesApi.model.GuestCount;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.model.PackageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BoostPackageRepositoryTest {

    @Autowired
    private BoostPackageRepository boostPackageRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private HotelPackage testHotelPackage1; // First HotelPackage
    private HotelPackage testHotelPackage2; // Second HotelPackage
    private BoostPackage testBoostPackage1;
    private BoostPackage testBoostPackage2;

    @BeforeEach
    void setup() {
        // Clear repositories
        boostPackageRepository.deleteAll();
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        // Setup Hotel
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        testHotel = hotelRepository.save(testHotel);

        // Setup GuestCount
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);

        // Setup HotelPackage 1
        testHotelPackage1 = new HotelPackage();
        testHotelPackage1.setName("Test Package 1");
        testHotelPackage1.setPrice(100.0);
        testHotelPackage1.setDescription("A test package 1");
        testHotelPackage1.setStartDate(LocalDate.now());
        testHotelPackage1.setEndDate(LocalDate.now().plusDays(7));
        testHotelPackage1.setTermsAndCondition("No refunds");
        testHotelPackage1.setVisitorCount(0);
        testHotelPackage1.setStatus(true);
        testHotelPackage1.setPackageStatus(PackageStatus.APPROVED);
        testHotelPackage1.setGuestCount(guestCount);
        testHotelPackage1.setHotel(testHotel);
        testHotelPackage1 = packageRepository.save(testHotelPackage1);

        // Setup HotelPackage 2
        testHotelPackage2 = new HotelPackage();
        testHotelPackage2.setName("Test Package 2");
        testHotelPackage2.setPrice(200.0);
        testHotelPackage2.setDescription("A test package 2");
        testHotelPackage2.setStartDate(LocalDate.now());
        testHotelPackage2.setEndDate(LocalDate.now().plusDays(7));
        testHotelPackage2.setTermsAndCondition("No refunds");
        testHotelPackage2.setVisitorCount(0);
        testHotelPackage2.setStatus(true);
        testHotelPackage2.setPackageStatus(PackageStatus.APPROVED);
        testHotelPackage2.setGuestCount(guestCount);
        testHotelPackage2.setHotel(testHotel);
        testHotelPackage2 = packageRepository.save(testHotelPackage2);

        // Setup BoostPackage 1
        testBoostPackage1 = new BoostPackage();
        testBoostPackage1.setBoostedDate(LocalDate.of(2025, 4, 10));
        testBoostPackage1.setHotelPackage(testHotelPackage1); // Unique HotelPackage
        testBoostPackage1.setHotel(testHotel);

        // Setup BoostPackage 2
        testBoostPackage2 = new BoostPackage();
        testBoostPackage2.setBoostedDate(LocalDate.of(2025, 4, 15));
        testBoostPackage2.setHotelPackage(testHotelPackage2); // Unique HotelPackage
        testBoostPackage2.setHotel(testHotel);
    }

    @Test
    void testSaveAndFindById() {
        // Save BoostPackage
        BoostPackage savedBoostPackage = boostPackageRepository.save(testBoostPackage1);

        // Verify save
        assertNotNull(savedBoostPackage);
        assertNotNull(savedBoostPackage.getId());

        // Find by ID
        Optional<BoostPackage> foundBoostPackage = boostPackageRepository.findById(savedBoostPackage.getId());
        assertTrue(foundBoostPackage.isPresent());
        assertEquals(savedBoostPackage.getId(), foundBoostPackage.get().getId());
        assertEquals(LocalDate.of(2025, 4, 10), foundBoostPackage.get().getBoostedDate());
        assertEquals(testHotelPackage1.getId(), foundBoostPackage.get().getHotelPackage().getId());
        assertEquals(testHotel.getId(), foundBoostPackage.get().getHotel().getId());
    }

    @Test
    void testFindAll() {
        // Save multiple BoostPackages
        boostPackageRepository.save(testBoostPackage1);
        boostPackageRepository.save(testBoostPackage2);

        // Find all
        List<BoostPackage> boostPackages = boostPackageRepository.findAll();
        assertEquals(2, boostPackages.size());

        // Verify contents
        assertTrue(boostPackages.stream().anyMatch(bp -> bp.getBoostedDate().equals(LocalDate.of(2025, 4, 10))));
        assertTrue(boostPackages.stream().anyMatch(bp -> bp.getBoostedDate().equals(LocalDate.of(2025, 4, 15))));
        assertTrue(boostPackages.stream().anyMatch(bp -> bp.getHotelPackage().getId().equals(testHotelPackage1.getId())));
        assertTrue(boostPackages.stream().anyMatch(bp -> bp.getHotelPackage().getId().equals(testHotelPackage2.getId())));
        assertTrue(boostPackages.stream().allMatch(bp -> bp.getHotel().getId().equals(testHotel.getId())));
    }

    @Test
    void testFindAllWhenEmpty() {
        // No BoostPackages saved
        List<BoostPackage> boostPackages = boostPackageRepository.findAll();
        assertTrue(boostPackages.isEmpty());
    }

    @Test
    void testDeleteBoostPackage() {
        // Save BoostPackage
        BoostPackage savedBoostPackage = boostPackageRepository.save(testBoostPackage1);
        assertNotNull(savedBoostPackage.getId());

        // Delete BoostPackage
        boostPackageRepository.delete(savedBoostPackage);

        // Verify deletion
        Optional<BoostPackage> deletedBoostPackage = boostPackageRepository.findById(savedBoostPackage.getId());
        assertFalse(deletedBoostPackage.isPresent());
    }

    @Test
    void testCountBoostPackages() {
        // Initial count should be 0
        assertEquals(0, boostPackageRepository.count());

        // Save BoostPackages
        boostPackageRepository.save(testBoostPackage1);
        boostPackageRepository.save(testBoostPackage2);

        // Verify count
        assertEquals(2, boostPackageRepository.count());
    }

    @Test
    void testSaveWithNullFields() {
        // Testней saving BoostPackage with minimal required fields
        BoostPackage minimalBoostPackage = new BoostPackage();
        minimalBoostPackage.setBoostedDate(LocalDate.of(2025, 4, 20));
        minimalBoostPackage.setHotelPackage(testHotelPackage1); // Use testHotelPackage1
        minimalBoostPackage.setHotel(testHotel);

        BoostPackage savedBoostPackage = boostPackageRepository.save(minimalBoostPackage);
        assertNotNull(savedBoostPackage.getId());

        // Verify saved data
        Optional<BoostPackage> foundBoostPackage = boostPackageRepository.findById(savedBoostPackage.getId());
        assertTrue(foundBoostPackage.isPresent());
        assertEquals(LocalDate.of(2025, 4, 20), foundBoostPackage.get().getBoostedDate());
        assertEquals(testHotelPackage1.getId(), foundBoostPackage.get().getHotelPackage().getId());
        assertEquals(testHotel.getId(), foundBoostPackage.get().getHotel().getId());
    }
}