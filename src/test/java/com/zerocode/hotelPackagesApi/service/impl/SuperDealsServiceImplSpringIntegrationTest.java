//package com.zerocode.hotelPackagesApi.service.impl;
//
//import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
//import com.zerocode.hotelPackagesApi.exception.NotCreatedException;
//import com.zerocode.hotelPackagesApi.exception.SuperDealNotFoundException;
//import com.zerocode.hotelPackagesApi.model.Hotel;
//import com.zerocode.hotelPackagesApi.model.HotelPackage;
//import com.zerocode.hotelPackagesApi.model.SuperDeal;
//import com.zerocode.hotelPackagesApi.repository.HotelRepository;
//import com.zerocode.hotelPackagesApi.repository.PackageRepository;
//import com.zerocode.hotelPackagesApi.repository.SuperDealsRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class SuperDealsServiceImplSpringIntegrationTest {
//
//    @Autowired
//    private SuperDealsServiceImpl superDealsService;
//
//    @Autowired
//    private SuperDealsRepository superDealsRepository;
//
//    @Autowired
//    private PackageRepository packageRepository;
//
//    @Autowired
//    private HotelRepository hotelRepository;
//
//    private HotelPackage savedHotelPackage;
//
//    @BeforeEach
//    void setUp() {
//        superDealsRepository.deleteAll();
//        packageRepository.deleteAll();
//        hotelRepository.deleteAll();
//
//        Hotel hotel = new Hotel();
//        hotel.setName("Test Hotel");
//        Hotel savedHotel = hotelRepository.save(hotel);
//
//        HotelPackage hotelPackage = new HotelPackage();
//        hotelPackage.setName("Test Package");
//        hotelPackage.setPrice(199.99);
//        hotelPackage.setDescription("Test Description");
//        hotelPackage.setHotel(savedHotel);
//        savedHotelPackage = packageRepository.save(hotelPackage);
//    }
//
//    @Test
//    void create_Success() throws NotCreatedException {
//        CreateSuperDealsRequest request = new CreateSuperDealsRequest(
//                LocalDate.now(),
//                LocalDate.now().plusDays(7),
//                99.99
//        );
//
//        superDealsService.create(savedHotelPackage.getId(), request);
//
//        List<SuperDeal> superDeals = superDealsRepository.findAll();
//        assertEquals(1, superDeals.size());
//        SuperDeal created = superDeals.get(0);
//        assertEquals(99.99, created.getDiscountedPrice());
//        assertEquals(savedHotelPackage.getId(), created.getHotelPackage().getId());
//        assertNotNull(created.getHotel());
//    }
//
//    @Test
//    void create_PackageNotFound() {
//        CreateSuperDealsRequest request = new CreateSuperDealsRequest(
//                LocalDate.now(),
//                LocalDate.now().plusDays(7),
//                99.99
//        );
//
//        NotCreatedException exception = assertThrows(NotCreatedException.class,
//                () -> superDealsService.create(999L, request));
//
//        assertEquals("Package with id 999 not found", exception.getMessage());
//        assertEquals(0, superDealsRepository.count());
//    }
//
//    @Test
//    void findAll_Success() {
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setStartDate(LocalDate.now());
//        superDeal.setEndDate(LocalDate.now().plusDays(7));
//        superDeal.setDiscountedPrice(99.99);
//        superDeal.setHotelPackage(savedHotelPackage);
//        superDeal.setHotel(savedHotelPackage.getHotel());
//        superDealsRepository.save(superDeal);
//
//        List<SuperDeal> result = superDealsService.findAll();
//
//        assertEquals(1, result.size());
//        assertEquals(99.99, result.get(0).getDiscountedPrice());
//    }
//
//    @Test
//    void findAll_EmptyList() {
//        List<SuperDeal> result = superDealsService.findAll();
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void findById_Success() throws SuperDealNotFoundException {
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setStartDate(LocalDate.now());
//        superDeal.setEndDate(LocalDate.now().plusDays(7));
//        superDeal.setDiscountedPrice(99.99);
//        superDeal.setHotelPackage(savedHotelPackage);
//        superDeal.setHotel(savedHotelPackage.getHotel());
//        SuperDeal savedSuperDeal = superDealsRepository.save(superDeal);
//
//        SuperDeal result = superDealsService.findById(savedSuperDeal.getId());
//
//        assertNotNull(result);
//        assertEquals(99.99, result.getDiscountedPrice());
//        assertEquals(savedHotelPackage.getId(), result.getHotelPackage().getId());
//    }
//
//    @Test
//    void findById_NotFound() {
//        SuperDealNotFoundException exception = assertThrows(SuperDealNotFoundException.class,
//                () -> superDealsService.findById(999L));
//
//        assertEquals("SuperDeal not found with id 999", exception.getMessage());
//    }
//
//    @Test
//    void deleteById_Success() throws SuperDealNotFoundException {
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setStartDate(LocalDate.now());
//        superDeal.setEndDate(LocalDate.now().plusDays(7));
//        superDeal.setDiscountedPrice(99.99);
//        superDeal.setHotelPackage(savedHotelPackage);
//        superDeal.setHotel(savedHotelPackage.getHotel());
//        SuperDeal savedSuperDeal = superDealsRepository.save(superDeal);
//
//        superDealsService.deleteById(savedSuperDeal.getId());
//
//        assertEquals(0, superDealsRepository.count());
//        HotelPackage updatedPackage = packageRepository.findById(savedHotelPackage.getId()).get();
//        assertNull(updatedPackage.getSuperDeals());
//    }
//
//    @Test
//    void deleteById_NotFound() {
//        SuperDealNotFoundException exception = assertThrows(SuperDealNotFoundException.class,
//                () -> superDealsService.deleteById(999L));
//
//        assertEquals("SuperDeal not found with id 999", exception.getMessage());
//    }
//
//    @Test
//    void deleteById_NoHotelPackage() throws SuperDealNotFoundException {
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setStartDate(LocalDate.now());
//        superDeal.setEndDate(LocalDate.now().plusDays(7));
//        superDeal.setDiscountedPrice(99.99);
//        // No hotel package set
//        superDeal.setHotel(savedHotelPackage.getHotel());
//        SuperDeal savedSuperDeal = superDealsRepository.save(superDeal);
//
//        superDealsService.deleteById(savedSuperDeal.getId());
//
//        assertEquals(0, superDealsRepository.count());
//    }
//}