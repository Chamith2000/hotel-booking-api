package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.PackageController;
import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageList;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageListResponse;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.exception.BoostPackageNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.BoostPackage;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.repository.BoostPackageRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoostPackageServiceImplUnitTest {

   @Mock
   private BoostPackageRepository boostPackageRepository;

   @Mock
   private PackageRepository packageRepository;

   @Mock
   private PackageController packageController;

   @Mock
   private PackageRepository hotelPackageRepository;

   @InjectMocks
   private BoostPackageServiceImpl boostPackageService;

   private HotelPackage hotelPackage;
   private BoostPackage boostPackage;
   private PackageListItem packageListItem;  // Added missing field

   @BeforeEach
   void setUp() {
       hotelPackage = new HotelPackage();
       hotelPackage.setId(1L);
       hotelPackage.setName("Test Package");
       hotelPackage.setPrice(100.0);

       Hotel hotel = new Hotel();
       hotelPackage.setHotel(hotel);

       boostPackage = new BoostPackage();
       boostPackage.setId(1L);
       boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
       boostPackage.setHotelPackage(hotelPackage);

       // Initialize packageListItem
       packageListItem = new PackageListItem();
       packageListItem.setName("Test Package");
       packageListItem.setPrice(100.0);
       packageListItem.setGuestAdults(2);
       packageListItem.setGuestChildren(1);
   }

//    @Test
//    void createBoostPackage_Success() throws PackageNotFoundException {
//        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));
//
//        when(packageRepository.findById(1L)).thenReturn(Optional.of(hotelPackage));
//        when(boostPackageRepository.save(any(BoostPackage.class))).thenReturn(boostPackage);
//
//        // Call the method first
//        boostPackageService.createBoostPackage(1L, requestDTO);
//
//        // Then verify
//        verify(packageRepository, times(1)).findById(1L);
//        verify(boostPackageRepository, times(1)).save(any(BoostPackage.class));
//    }

//    @Test
//    void createBoostPackage_PackageNotFound() {
//        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(
//                LocalDate.of(2025, 4, 10)
//        );
//
//        // Configure mock to return empty Optional
//        when(packageRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Execute and verify exception
//        assertThrows(PackageNotFoundException.class, () -> {
//            boostPackageService.createBoostPackage(1L, requestDTO);
//        });
//
//        // Verify the interaction occurred
//        verify(packageRepository, times(1)).findById(1L);
//        verify(boostPackageRepository, never()).save(any());
//    }

   @Test
   void getBoostPackageById_Success() throws BoostPackageNotFoundException, PackageNotFoundException {
       when(boostPackageRepository.findById(1L)).thenReturn(Optional.of(boostPackage));
       when(packageController.getPackageById(1L)).thenReturn(packageListItem);

       BoostPackageList result = boostPackageService.getBoostPackageById(1L);

       assertNotNull(result);
       assertEquals(1L, result.getId());
       assertEquals("Test Package", result.getName());
       assertEquals(100.0, result.getPrice());
       verify(boostPackageRepository).findById(1L);
       verify(packageController).getPackageById(1L);
   }

   @Test
   void getBoostPackageById_BoostPackageNotFound() throws PackageNotFoundException {
       when(boostPackageRepository.findById(1L)).thenReturn(Optional.empty());

       assertThrows(BoostPackageNotFoundException.class, () ->
               boostPackageService.getBoostPackageById(1L));

       verify(boostPackageRepository).findById(1L);
       verify(packageController, never()).getPackageById(any());
   }

//    @Test
//    void deleteBoostPackage_Success() throws BoostPackageNotFoundException {
//        // Ensure the hotelPackage reference is properly set
//        boostPackage.setHotelPackage(hotelPackage);
//        hotelPackage.setBoostPackage(boostPackage);  // Set the bidirectional relationship
//
//        // Mock the repository behaviors
//        when(boostPackageRepository.findById(1L)).thenReturn(Optional.of(boostPackage));
//        when(hotelPackageRepository.save(any(HotelPackage.class))).thenReturn(hotelPackage);
//
//        // Execute the method
//        boostPackageService.deleteBoostPackage(1L);
//
//        // Verify all expected interactions
//        verify(boostPackageRepository, times(1)).findById(1L);
//        verify(hotelPackageRepository, times(1)).save(any(HotelPackage.class));
//        verify(boostPackageRepository, times(1)).delete(boostPackage);
//    }

   @Test
   void deleteBoostPackage_NotFound() {
       when(boostPackageRepository.findById(1L)).thenReturn(Optional.empty());

       assertThrows(BoostPackageNotFoundException.class, () ->
               boostPackageService.deleteBoostPackage(1L));

       verify(boostPackageRepository).findById(1L);
       verify(hotelPackageRepository, never()).save(any());
       verify(boostPackageRepository, never()).delete(any());
   }

   @Test
   void getBoostPackages_Success() throws BoostPackageNotFoundException, PackageNotFoundException {
       List<BoostPackage> boostPackages = Collections.singletonList(boostPackage);
       when(boostPackageRepository.findAll()).thenReturn(boostPackages);
       when(packageController.getPackageById(1L)).thenReturn(packageListItem);

       BoostPackageListResponse result = boostPackageService.getBoostPackages();

       assertNotNull(result);
       assertEquals(1, result.getBoostPackageLists().size());
       assertEquals("Test Package", result.getBoostPackageLists().get(0).getName());
       verify(boostPackageRepository).findAll();
       verify(packageController).getPackageById(1L);
   }

   @Test
   void getBoostPackages_EmptyList() throws PackageNotFoundException {
       when(boostPackageRepository.findAll()).thenReturn(Collections.emptyList());

       assertThrows(BoostPackageNotFoundException.class, () ->
               boostPackageService.getBoostPackages());

       verify(boostPackageRepository).findAll();
       verify(packageController, never()).getPackageById(any());
   }
}