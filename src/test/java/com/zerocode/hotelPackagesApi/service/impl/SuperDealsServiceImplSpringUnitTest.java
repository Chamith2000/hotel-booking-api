//package com.zerocode.hotelPackagesApi.service.impl;
//
//import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
//import com.zerocode.hotelPackagesApi.exception.NotCreatedException;
//import com.zerocode.hotelPackagesApi.exception.SuperDealNotFoundException;
//import com.zerocode.hotelPackagesApi.model.Hotel;
//import com.zerocode.hotelPackagesApi.model.HotelPackage;
//import com.zerocode.hotelPackagesApi.model.SuperDeal;
//import com.zerocode.hotelPackagesApi.repository.PackageRepository;
//import com.zerocode.hotelPackagesApi.repository.SuperDealsRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class SuperDealsServiceImplSpringUnitTest {
//
//    @Mock
//    private PackageRepository packageRepository;
//
//    @Mock
//    private SuperDealsRepository superDealsRepository;
//
//    @InjectMocks
//    private SuperDealsServiceImpl superDealsService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void create_Success() throws NotCreatedException {
//        Long packageId = 1L;
//        CreateSuperDealsRequest request = new CreateSuperDealsRequest(
//                LocalDate.now(),
//                LocalDate.now().plusDays(7),
//                99.99
//        );
//
//        Hotel hotel = new Hotel();
//        hotel.setId(1L);
//        HotelPackage hotelPackage = new HotelPackage();
//        hotelPackage.setId(packageId);
//        hotelPackage.setHotel(hotel);
//
//        when(packageRepository.findById(packageId)).thenReturn(Optional.of(hotelPackage));
//        when(superDealsRepository.save(any(SuperDeal.class))).thenAnswer(invocation -> {
//            SuperDeal superDeal = invocation.getArgument(0);
//            superDeal.setId(1L);
//            return superDeal;
//        });
//
//        superDealsService.create(packageId, request);
//
//        verify(packageRepository, times(3)).findById(packageId);  // Changed from 2 to 3
//        verify(superDealsRepository, times(1)).save(any(SuperDeal.class));
//    }
//
//    @Test
//    void create_PackageNotFound() {
//        Long packageId = 1L;
//        CreateSuperDealsRequest request = new CreateSuperDealsRequest(
//                LocalDate.now(),
//                LocalDate.now().plusDays(7),
//                99.99
//        );
//
//        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());
//
//        NotCreatedException exception = assertThrows(NotCreatedException.class,
//                () -> superDealsService.create(packageId, request));
//
//        assertEquals("Package with id 1 not found", exception.getMessage());
//        verify(packageRepository, times(1)).findById(packageId);
//        verify(superDealsRepository, never()).save(any());
//    }
//
//    @Test
//    void findAll_Success() {
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setId(1L);
//        superDeal.setDiscountedPrice(99.99);
//
//        when(superDealsRepository.findAll()).thenReturn(Collections.singletonList(superDeal));
//
//        List<SuperDeal> result = superDealsService.findAll();
//
//        assertEquals(1, result.size());
//        assertEquals(99.99, result.get(0).getDiscountedPrice());
//        verify(superDealsRepository, times(1)).findAll();
//    }
//
//    @Test
//    void findAll_EmptyList() {
//        when(superDealsRepository.findAll()).thenReturn(Collections.emptyList());
//
//        List<SuperDeal> result = superDealsService.findAll();
//
//        assertTrue(result.isEmpty());
//        verify(superDealsRepository, times(1)).findAll();
//    }
//
//    @Test
//    void findById_Success() throws SuperDealNotFoundException {
//        Long superDealId = 1L;
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setId(superDealId);
//        superDeal.setDiscountedPrice(99.99);
//
//        when(superDealsRepository.findById(superDealId)).thenReturn(Optional.of(superDeal));
//
//        SuperDeal result = superDealsService.findById(superDealId);
//
//        assertNotNull(result);
//        assertEquals(99.99, result.getDiscountedPrice());
//        verify(superDealsRepository, times(1)).findById(superDealId);
//    }
//
//    @Test
//    void findById_NotFound() {
//        Long superDealId = 1L;
//        when(superDealsRepository.findById(superDealId)).thenReturn(Optional.empty());
//
//        SuperDealNotFoundException exception = assertThrows(SuperDealNotFoundException.class,
//                () -> superDealsService.findById(superDealId));
//
//        assertEquals("SuperDeal not found with id 1", exception.getMessage());
//        verify(superDealsRepository, times(1)).findById(superDealId);
//    }
//
//    @Test
//    void deleteById_Success() throws SuperDealNotFoundException {
//        Long superDealId = 1L;
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setId(superDealId);
//        HotelPackage hotelPackage = new HotelPackage();
//        hotelPackage.setId(1L);
//        superDeal.setHotelPackage(hotelPackage);
//
//        when(superDealsRepository.findById(superDealId)).thenReturn(Optional.of(superDeal));
//        doNothing().when(superDealsRepository).delete(superDeal);
//
//        superDealsService.deleteById(superDealId);
//
//        verify(superDealsRepository, times(1)).findById(superDealId);
//        verify(packageRepository, times(1)).save(hotelPackage);
//        verify(superDealsRepository, times(1)).delete(superDeal);
//    }
//
//    @Test
//    void deleteById_NotFound() {
//        Long superDealId = 1L;
//        when(superDealsRepository.findById(superDealId)).thenReturn(Optional.empty());
//
//        SuperDealNotFoundException exception = assertThrows(SuperDealNotFoundException.class,
//                () -> superDealsService.deleteById(superDealId));
//
//        assertEquals("SuperDeal not found with id 1", exception.getMessage());
//        verify(superDealsRepository, times(1)).findById(superDealId);
//        verify(packageRepository, never()).save(any());
//        verify(superDealsRepository, never()).delete(any());
//    }
//
//    @Test
//    void deleteById_NoHotelPackage() throws SuperDealNotFoundException {
//        Long superDealId = 1L;
//        SuperDeal superDeal = new SuperDeal();
//        superDeal.setId(superDealId);
//        superDeal.setHotelPackage(null);
//
//        when(superDealsRepository.findById(superDealId)).thenReturn(Optional.of(superDeal));
//        doNothing().when(superDealsRepository).delete(superDeal);
//
//        superDealsService.deleteById(superDealId);
//
//        verify(superDealsRepository, times(1)).findById(superDealId);
//        verify(packageRepository, never()).save(any());
//        verify(superDealsRepository, times(1)).delete(superDeal);
//    }
//}