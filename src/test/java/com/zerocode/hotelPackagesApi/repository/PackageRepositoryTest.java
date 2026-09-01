package com.zerocode.hotelPackagesApi.repository;

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
class PackageRepositoryTest {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private HotelPackage testPackage1;
    private HotelPackage testPackage2;

    @BeforeEach
    void setup() {
        // Clear both repositories
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        // Setup Hotel
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");  // Required field
        testHotel.setPassword("password");     // Required field
        testHotel.setStatus(true);             // Required field
        testHotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        testHotel = hotelRepository.save(testHotel);

        // Setup GuestCount
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);

        // Setup HotelPackage 1
        testPackage1 = new HotelPackage();
        testPackage1.setName("Package 1");
        testPackage1.setPrice(100.0);                    // Required
        testPackage1.setDescription("Test Package 1");   // Required
        testPackage1.setStartDate(LocalDate.now());      // Required
        testPackage1.setEndDate(LocalDate.now().plusDays(7));  // Required
        testPackage1.setTermsAndCondition("No refunds"); // Required
        testPackage1.setVisitorCount(0);                 // Required
        testPackage1.setStatus(true);                    // Required
        testPackage1.setPackageStatus(PackageStatus.APPROVED);  // Using provided enum
        testPackage1.setGuestCount(guestCount);          // Using provided class
        testPackage1.setHotel(testHotel);                // Required

        // Setup HotelPackage 2
        testPackage2 = new HotelPackage();
        testPackage2.setName("Package 2");
        testPackage2.setPrice(200.0);                    // Required
        testPackage2.setDescription("Test Package 2");   // Required
        testPackage2.setStartDate(LocalDate.now());      // Required
        testPackage2.setEndDate(LocalDate.now().plusDays(7));  // Required
        testPackage2.setTermsAndCondition("No refunds"); // Required
        testPackage2.setVisitorCount(0);                 // Required
        testPackage2.setStatus(false);                   // Required
        testPackage2.setPackageStatus(PackageStatus.REJECTED);  // Using provided enum
        testPackage2.setGuestCount(guestCount);          // Using provided class
        testPackage2.setHotel(testHotel);                // Required
    }

    @Test
    void testSaveAndFindById() {
        // Save package
        HotelPackage savedPackage = packageRepository.save(testPackage1);

        // Verify save
        assertNotNull(savedPackage);
        assertNotNull(savedPackage.getId());

        // Find by ID
        Optional<HotelPackage> foundPackage = packageRepository.findById(savedPackage.getId());
        assertTrue(foundPackage.isPresent());
        assertEquals(savedPackage.getId(), foundPackage.get().getId());
        assertEquals("Package 1", foundPackage.get().getName());
        assertEquals(true, foundPackage.get().getStatus());
        assertEquals(PackageStatus.APPROVED, foundPackage.get().getPackageStatus());
        assertEquals(testHotel.getId(), foundPackage.get().getHotel().getId());
        assertEquals(2, foundPackage.get().getGuestCount().getAdults());
        assertEquals(1, foundPackage.get().getGuestCount().getChildren());
    }

    @Test
    void testFindByStatus() {
        // Save multiple packages
        packageRepository.save(testPackage1);  // status = true
        packageRepository.save(testPackage2);  // status = false

        // Find active packages (status = true)
        List<HotelPackage> activePackages = packageRepository.findByStatus(true);
        assertEquals(1, activePackages.size());
        assertEquals("Package 1", activePackages.get(0).getName());
        assertTrue(activePackages.get(0).getStatus());
        assertEquals(PackageStatus.APPROVED, activePackages.get(0).getPackageStatus());

        // Find inactive packages (status = false)
        List<HotelPackage> inactivePackages = packageRepository.findByStatus(false);
        assertEquals(1, inactivePackages.size());
        assertEquals("Package 2", inactivePackages.get(0).getName());
        assertFalse(inactivePackages.get(0).getStatus());
        assertEquals(PackageStatus.REJECTED, inactivePackages.get(0).getPackageStatus());
    }

    @Test
    void testFindByHotelId() {
        // Save packages
        packageRepository.save(testPackage1);
        packageRepository.save(testPackage2);

        // Find packages by hotel ID
        List<HotelPackage> hotelPackages = packageRepository.findByHotelId(testHotel.getId());
        assertEquals(2, hotelPackages.size());

        // Verify both packages are found
        assertTrue(hotelPackages.stream().anyMatch(p -> p.getName().equals("Package 1")));
        assertTrue(hotelPackages.stream().anyMatch(p -> p.getName().equals("Package 2")));
        assertTrue(hotelPackages.stream().allMatch(p -> p.getHotel().getId().equals(testHotel.getId())));
    }

    @Test
    void testFindByHotelIdNotExisting() {
        // Save packages
        packageRepository.save(testPackage1);
        packageRepository.save(testPackage2);

        // Find packages for non-existing hotel ID
        List<HotelPackage> hotelPackages = packageRepository.findByHotelId(999L);
        assertTrue(hotelPackages.isEmpty());
    }

    @Test
    void testDeletePackage() {
        // Save package
        HotelPackage savedPackage = packageRepository.save(testPackage1);
        assertNotNull(savedPackage.getId());

        // Delete package
        packageRepository.delete(savedPackage);

        // Verify deletion
        Optional<HotelPackage> deletedPackage = packageRepository.findById(savedPackage.getId());
        assertFalse(deletedPackage.isPresent());
    }

    @Test
    void testCountPackages() {
        // Initial count should be 0
        assertEquals(0, packageRepository.count());

        // Save packages
        packageRepository.save(testPackage1);
        packageRepository.save(testPackage2);

        // Verify count
        assertEquals(2, packageRepository.count());
    }
}