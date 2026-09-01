package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.model.SuperDeal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SuperDealsRepositoryTest {

    @Autowired
    private SuperDealsRepository superDealsRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Hotel savedHotel;
    private HotelPackage savedHotelPackage;

    @BeforeEach
    public void clearDB() {
        // Clear dependent data in correct order to avoid FK constraints
        superDealsRepository.deleteAll();
        packageRepository.deleteAll();
        // Use TestEntityManager to clear HotelContactNumber
        entityManager.getEntityManager().createQuery("DELETE FROM HotelContactNumber").executeUpdate();
        hotelRepository.deleteAll();

        // Set up Hotel
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        savedHotel = hotelRepository.save(hotel);

        // Set up HotelPackage
        HotelPackage hotelPackage = new HotelPackage();
        hotelPackage.setName("Test Package");
        hotelPackage.setPrice(199.99);
        hotelPackage.setDescription("Test Description");
        hotelPackage.setHotel(savedHotel);
        savedHotelPackage = packageRepository.save(hotelPackage);
    }

    @Test
    public void testSaveSuperDeal() {
        SuperDeal superDeal = new SuperDeal();
        superDeal.setStartDate(LocalDate.now());
        superDeal.setEndDate(LocalDate.now().plusDays(7));
        superDeal.setDiscountedPrice(99.99);
        superDeal.setHotelPackage(savedHotelPackage);
        superDeal.setHotel(savedHotel);

        SuperDeal savedSuperDeal = superDealsRepository.save(superDeal);

        Assertions.assertNotNull(savedSuperDeal);
        Assertions.assertNotNull(savedSuperDeal.getId());
        Assertions.assertEquals(99.99, savedSuperDeal.getDiscountedPrice());
        Assertions.assertEquals(savedHotelPackage.getId(), savedSuperDeal.getHotelPackage().getId());
        Assertions.assertEquals(savedHotel.getId(), savedSuperDeal.getHotel().getId());
    }

    @Test
    public void testGetSuperDealById() {
        SuperDeal superDeal = new SuperDeal();
        superDeal.setStartDate(LocalDate.now());
        superDeal.setEndDate(LocalDate.now().plusDays(7));
        superDeal.setDiscountedPrice(99.99);
        superDeal.setHotelPackage(savedHotelPackage);
        superDeal.setHotel(savedHotel);
        SuperDeal createdSuperDeal = superDealsRepository.save(superDeal);

        Assertions.assertNotNull(createdSuperDeal);
        Assertions.assertNotNull(createdSuperDeal.getId());

        Optional<SuperDeal> foundSuperDeal = superDealsRepository.findById(createdSuperDeal.getId());
        Assertions.assertTrue(foundSuperDeal.isPresent());
        Assertions.assertEquals(createdSuperDeal.getId(), foundSuperDeal.get().getId());
        Assertions.assertEquals(createdSuperDeal.getDiscountedPrice(), foundSuperDeal.get().getDiscountedPrice());
        Assertions.assertEquals(createdSuperDeal.getHotelPackage().getId(), foundSuperDeal.get().getHotelPackage().getId());
        Assertions.assertEquals(createdSuperDeal.getHotel().getId(), foundSuperDeal.get().getHotel().getId());
    }

    @Test
    public void testGetSuperDealByNonExistingId() {
        SuperDeal superDeal = new SuperDeal();
        superDeal.setStartDate(LocalDate.now());
        superDeal.setEndDate(LocalDate.now().plusDays(7));
        superDeal.setDiscountedPrice(99.99);
        superDeal.setHotelPackage(savedHotelPackage);
        superDeal.setHotel(savedHotel);
        SuperDeal createdSuperDeal = superDealsRepository.save(superDeal);

        Assertions.assertNotNull(createdSuperDeal);
        Assertions.assertNotNull(createdSuperDeal.getId());

        Optional<SuperDeal> foundSuperDeal = superDealsRepository.findById(999L);
        Assertions.assertFalse(foundSuperDeal.isPresent());
    }

    @Test
    public void testFindAllSuperDeals() {
        // Create first HotelPackage
        HotelPackage hotelPackage1 = new HotelPackage();
        hotelPackage1.setName("Package 1");
        hotelPackage1.setPrice(199.99);
        hotelPackage1.setDescription("First Test Package");
        hotelPackage1.setHotel(savedHotel);
        HotelPackage savedHotelPackage1 = packageRepository.save(hotelPackage1);

        SuperDeal superDeal1 = new SuperDeal();
        superDeal1.setStartDate(LocalDate.now());
        superDeal1.setEndDate(LocalDate.now().plusDays(7));
        superDeal1.setDiscountedPrice(99.99);
        superDeal1.setHotelPackage(savedHotelPackage1);
        superDeal1.setHotel(savedHotel);
        superDealsRepository.save(superDeal1);

        // Create second HotelPackage
        HotelPackage hotelPackage2 = new HotelPackage();
        hotelPackage2.setName("Package 2");
        hotelPackage2.setPrice(299.99);
        hotelPackage2.setDescription("Second Test Package");
        hotelPackage2.setHotel(savedHotel);
        HotelPackage savedHotelPackage2 = packageRepository.save(hotelPackage2);

        SuperDeal superDeal2 = new SuperDeal();
        superDeal2.setStartDate(LocalDate.now());
        superDeal2.setEndDate(LocalDate.now().plusDays(14));
        superDeal2.setDiscountedPrice(89.99);
        superDeal2.setHotelPackage(savedHotelPackage2);
        superDeal2.setHotel(savedHotel);
        superDealsRepository.save(superDeal2);

        List<SuperDeal> allSuperDeals = superDealsRepository.findAll();

        Assertions.assertEquals(2, allSuperDeals.size());
        Assertions.assertTrue(allSuperDeals.stream().anyMatch(sd -> sd.getDiscountedPrice() == 99.99));
        Assertions.assertTrue(allSuperDeals.stream().anyMatch(sd -> sd.getDiscountedPrice() == 89.99));
    }

    @Test
    public void testDeleteSuperDeal() {
        SuperDeal superDeal = new SuperDeal();
        superDeal.setStartDate(LocalDate.now());
        superDeal.setEndDate(LocalDate.now().plusDays(7));
        superDeal.setDiscountedPrice(99.99);
        superDeal.setHotelPackage(savedHotelPackage);
        superDeal.setHotel(savedHotel);
        SuperDeal createdSuperDeal = superDealsRepository.save(superDeal);

        Assertions.assertNotNull(createdSuperDeal);
        Assertions.assertNotNull(createdSuperDeal.getId());

        superDealsRepository.deleteById(createdSuperDeal.getId());

        Optional<SuperDeal> foundSuperDeal = superDealsRepository.findById(createdSuperDeal.getId());
        Assertions.assertFalse(foundSuperDeal.isPresent());
    }
}