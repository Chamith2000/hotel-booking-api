package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import com.zerocode.hotelPackagesApi.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PackageServiceImplUnitTest {

   @Mock
   private PackageRepository packageRepository;

   @Mock
   private HotelRepository hotelRepository;

   @Mock
   private NotificationService notificationService;

   @InjectMocks
   private PackageServiceImpl packageService;

   private Hotel hotel;
   private HotelPackage hotelPackage;
   private CreatePackageRequestDTO packageRequestDTO;

   @BeforeEach
   public void setUp() {
       // Setup common test data
       hotel = new Hotel();
       hotel.setId(1L);
       hotel.setName("Test Hotel");

       hotelPackage = new HotelPackage();
       hotelPackage.setId(1L);
       hotelPackage.setName("Test Package");
       hotelPackage.setDescription("Test Description");
       hotelPackage.setPrice(100.00);
       hotelPackage.setStartDate(LocalDate.now());
       hotelPackage.setEndDate(LocalDate.now().plusDays(7));
       hotelPackage.setHotel(hotel);

       packageRequestDTO = new CreatePackageRequestDTO();
       packageRequestDTO.setName("New Package");
       packageRequestDTO.setDescription("New Description");
       packageRequestDTO.setPrice(150.00);
       packageRequestDTO.setStartDate(LocalDate.now());
       packageRequestDTO.setEndDate(LocalDate.now().plusDays(10));
       packageRequestDTO.setImages(new ArrayList<>());
   }

   @Test
   public void testCreatePackages_Success() throws HotelNotFoundException {
       // Arrange
       when(hotelRepository.findById(anyLong())).thenReturn(Optional.of(hotel));
       when(packageRepository.save(any(HotelPackage.class))).thenReturn(hotelPackage);

       // Act
       packageService.createPackages(1L, packageRequestDTO);

       // Assert
       verify(hotelRepository).findById(1L);
       verify(packageRepository).save(any(HotelPackage.class));
   }

   @Test
   public void testCreatePackages_HotelNotFound() {
       // Arrange
       when(hotelRepository.findById(anyLong())).thenReturn(Optional.empty());

       // Act & Assert
       assertThrows(HotelNotFoundException.class, () -> {
           packageService.createPackages(1L, packageRequestDTO);
       });
   }
//
   @Test
   public void testDeletePackageById_Success() throws PackageNotFoundException {
       // Arrange
       when(packageRepository.findById(anyLong())).thenReturn(Optional.of(hotelPackage));

       // Act
       packageService.deletePackageById(1L);

       // Assert
       verify(packageRepository).delete(hotelPackage);
   }

   @Test
   public void testDeletePackageById_PackageNotFound() {
       // Arrange
       when(packageRepository.findById(anyLong())).thenReturn(Optional.empty());

       // Act & Assert
       assertThrows(PackageNotFoundException.class, () -> {
           packageService.deletePackageById(1L);
       });
   }

   @Test
   public void testUpdatePackageById_Success() throws PackageNotFoundException {
       // Arrange
       when(packageRepository.findById(anyLong())).thenReturn(Optional.of(hotelPackage));
       when(packageRepository.save(any(HotelPackage.class))).thenReturn(hotelPackage);

       // Act
       packageService.updatePackageById(1L, packageRequestDTO);

       // Assert
       verify(packageRepository).findById(1L);
       verify(packageRepository).save(any(HotelPackage.class));
       assertEquals(packageRequestDTO.getName(), hotelPackage.getName());
   }

   @Test
   public void testUpdatePackageById_PackageNotFound() {
       // Arrange
       when(packageRepository.findById(anyLong())).thenReturn(Optional.empty());

       // Act & Assert
       assertThrows(PackageNotFoundException.class, () -> {
           packageService.updatePackageById(1L, packageRequestDTO);
       });
   }

   @Test
   public void testFindByHotelId() {
       // Arrange
       List<HotelPackage> packages = Arrays.asList(hotelPackage);
       when(packageRepository.findByHotelId(anyLong())).thenReturn(packages);

       // Act
       List<PackageListItem> result = packageService.findByHotelId(1L);

       // Assert
       assertNotNull(result);
       assertFalse(result.isEmpty());
       assertEquals(1, result.size());
   }

   @Test
   public void testFindAll() {
       // Arrange
       List<HotelPackage> packages = Arrays.asList(hotelPackage);
       when(packageRepository.findAll()).thenReturn(packages);

       // Act
       List<PackageListItem> result = packageService.findAll();

       // Assert
       assertNotNull(result);
       assertFalse(result.isEmpty());
       assertEquals(1, result.size());
   }

   @Test
   public void testFindById_Success() throws PackageNotFoundException {
       // Arrange
       when(packageRepository.findById(anyLong())).thenReturn(Optional.of(hotelPackage));

       // Act
       PackageListItem result = packageService.findById(1L);

       // Assert
       assertNotNull(result);
       assertEquals(hotelPackage.getName(), result.getName());
   }

   @Test
   public void testFindById_PackageNotFound() {
       // Arrange
       when(packageRepository.findById(anyLong())).thenReturn(Optional.empty());

       // Act & Assert
       assertThrows(PackageNotFoundException.class, () -> {
           packageService.findById(1L);
       });
   }

   @Test
   public void testFindByStatus() {
       // Arrange
       List<HotelPackage> packages = Arrays.asList(hotelPackage);
       when(packageRepository.findByStatus(anyBoolean())).thenReturn(packages);

       // Act
       List<PackageListItem> result = packageService.findByStatus(true);

       // Assert
       assertNotNull(result);
       assertFalse(result.isEmpty());
       assertEquals(1, result.size());
   }

   @Test
   public void testGetPackagesCount() {
       // Arrange
       when(packageRepository.count()).thenReturn(5L);

       // Act
       int count = packageService.getPackagesCount();

       // Assert
       assertEquals(5, count);
   }

   @Test
   public void testGetPackagesCountByStatus() {
       // Arrange
       List<HotelPackage> packages = Arrays.asList(hotelPackage);
       when(packageRepository.findByStatus(anyBoolean())).thenReturn(packages);

       // Act
       int count = packageService.getPackagesCountByStatus(true);

       // Assert
       assertEquals(1, count);
   }
}