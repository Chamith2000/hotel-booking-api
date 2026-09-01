package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.controller.response.SuperDealListItem;
import com.zerocode.hotelPackagesApi.controller.response.SuperDealResponse;
import com.zerocode.hotelPackagesApi.controller.response.SuperDealsListResponse;
import com.zerocode.hotelPackagesApi.exception.NotCreatedException;
import com.zerocode.hotelPackagesApi.exception.NotFoundException;
import com.zerocode.hotelPackagesApi.exception.SuperDealNotFoundException;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.model.SuperDeal;
import com.zerocode.hotelPackagesApi.service.SuperDealsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SuperDealsControllerMockitoUnitTest {

    @Mock
    private SuperDealsService superDealsService;

    @Mock
    private PackageController packageController;

    @InjectMocks
    private SuperDealsController superDealsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_SuccessfulCreation() throws NotCreatedException {
        Long packageId = 1L;
        CreateSuperDealsRequest request = new CreateSuperDealsRequest(
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                99.99
        );

        doNothing().when(superDealsService).create(packageId, request);

        assertDoesNotThrow(() -> superDealsController.create(packageId, request));
        verify(superDealsService, times(1)).create(packageId, request);
    }

    @Test
    void create_NotCreatedException() throws NotCreatedException {
        Long packageId = 1L;
        CreateSuperDealsRequest request = new CreateSuperDealsRequest(
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                99.99
        );

        doThrow(new NotCreatedException("Package not found"))
                .when(superDealsService).create(packageId, request);

        assertThrows(NotCreatedException.class,
                () -> superDealsController.create(packageId, request));
        verify(superDealsService, times(1)).create(packageId, request);
    }

    @Test
    void getAllSuperDeals_Success() {
        SuperDeal superDeal = new SuperDeal();
        superDeal.setId(1L);
        superDeal.setStartDate(LocalDate.now());
        superDeal.setEndDate(LocalDate.now().plusDays(7));
        superDeal.setDiscountedPrice(99.99);
        HotelPackage hotelPackage = new HotelPackage();
        hotelPackage.setId(1L);
        hotelPackage.setName("Test Package");
        superDeal.setHotelPackage(hotelPackage);

        when(superDealsService.findAll()).thenReturn(Collections.singletonList(superDeal));

        SuperDealsListResponse response = superDealsController.getAllSuperDeals();

        assertNotNull(response);
        assertEquals(1, response.getSuperDeals().size());
        assertEquals(99.99, response.getSuperDeals().get(0).getDiscountedPrice());
        verify(superDealsService, times(1)).findAll();
    }

    @Test
    void getAllSuperDeals_EmptyList() {
        when(superDealsService.findAll()).thenReturn(Collections.emptyList());

        SuperDealsListResponse response = superDealsController.getAllSuperDeals();

        assertNotNull(response);
        assertTrue(response.getSuperDeals().isEmpty());
        verify(superDealsService, times(1)).findAll();
    }

    @Test
    void getSuperDealById_Success() throws NotFoundException {
        Long superDealId = 1L;
        SuperDeal superDeal = new SuperDeal();
        superDeal.setId(superDealId);
        superDeal.setStartDate(LocalDate.now());
        superDeal.setEndDate(LocalDate.now().plusDays(7));
        superDeal.setDiscountedPrice(99.99);
        HotelPackage hotelPackage = new HotelPackage();
        hotelPackage.setId(1L);
        superDeal.setHotelPackage(hotelPackage);

        PackageListItem packageListItem = PackageListItem.builder()
                .id(1L)
                .name("Test Package")
                .build();

        when(superDealsService.findById(superDealId)).thenReturn(superDeal);
        when(packageController.getPackageById(1L)).thenReturn(packageListItem);

        SuperDealResponse response = superDealsController.getSuperDealById(superDealId);

        assertNotNull(response);
        assertNotNull(response.getSuperDealListItem());
        assertEquals(99.99, response.getSuperDealListItem().getDiscountedPrice());
        verify(superDealsService, times(1)).findById(superDealId);
        verify(packageController, times(1)).getPackageById(1L);
    }

    @Test
    void getSuperDealById_NotFound() throws SuperDealNotFoundException {
        Long superDealId = 1L;
        when(superDealsService.findById(superDealId))
                .thenThrow(new SuperDealNotFoundException("Super deal not found"));

        assertThrows(SuperDealNotFoundException.class,
                () -> superDealsController.getSuperDealById(superDealId));
        verify(superDealsService, times(1)).findById(superDealId);
    }

    @Test
    void deleteById_Success() throws SuperDealNotFoundException {
        Long superDealId = 1L;
        doNothing().when(superDealsService).deleteById(superDealId);

        assertDoesNotThrow(() -> superDealsController.deleteById(superDealId));
        verify(superDealsService, times(1)).deleteById(superDealId);
    }

    @Test
    void deleteById_NotFound() throws SuperDealNotFoundException {
        Long superDealId = 1L;
        doThrow(new SuperDealNotFoundException("Super deal not found"))
                .when(superDealsService).deleteById(superDealId);

        assertThrows(SuperDealNotFoundException.class,
                () -> superDealsController.deleteById(superDealId));
        verify(superDealsService, times(1)).deleteById(superDealId);
    }
}