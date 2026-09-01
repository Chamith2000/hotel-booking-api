package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.exception.HotelNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class HotelServiceImplSpringUnitTest {

   @InjectMocks
   private HotelServiceImpl hotelService;

   @Mock
   private HotelRepository hotelRepository;

   @Test
   @DisplayName("Try to create a hotel with an already registered email")
   public void testCreateHotelWithDuplicateEmail() {
       String email = "test@hotel.com";

       var existingHotel = new Hotel();
       existingHotel.setEmail(email);

       Mockito.when(hotelRepository.findByEmail(email)).thenReturn(Optional.of(existingHotel));

       var createHotelRequest = new CreateHotelRequest();
       createHotelRequest.setEmail(email);

       assertThrows(HotelNotCreatedException.class, () -> hotelService.createHotel(createHotelRequest));
   }

   @Test
   @DisplayName("Create a new hotel successfully")
   public void testCreateHotel() {
       String email = "newhotel@hotel.com";

       var createHotelRequest = new CreateHotelRequest();
       createHotelRequest.setName("New Hotel");
       createHotelRequest.setEmail(email);
       createHotelRequest.setPassword("password123");
       createHotelRequest.setBoostPackageLimit("5");
       createHotelRequest.setSuperDealLimit("3");
       createHotelRequest.setStatus(true);

       createHotelRequest.setContactNumbers(List.of());

       createHotelRequest.setHotelPhotos(List.of());

       Mockito.when(hotelRepository.findByEmail(email)).thenReturn(Optional.empty());
       Mockito.when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> invocation.getArgument(0));

       assertDoesNotThrow(() -> hotelService.createHotel(createHotelRequest));
   }


   @Test
   @DisplayName("Find hotel by ID - Hotel found")
   public void testFindHotelById() throws Exception {
       Long hotelId = 1L;
       var hotel = new Hotel();
       hotel.setId(hotelId);
       hotel.setName("Test Hotel");

       Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

       var foundHotel = hotelService.findById(hotelId);

       assertEquals(hotel.getId(), foundHotel.getId());
       assertEquals(hotel.getName(), foundHotel.getName());
   }

   @Test
   @DisplayName("Find hotel by ID - Hotel not found")
   public void testFindHotelById_NotFound() {
       Long hotelId = 1L;

       Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

       assertThrows(HotelNotFoundException.class, () -> hotelService.findById(hotelId));
   }

   @Test
   @DisplayName("Update hotel information")
   public void testUpdateHotel() throws Exception {
       Long hotelId = 1L;
       var hotel = new Hotel();
       hotel.setId(hotelId);
       hotel.setName("Old Hotel");
       hotel.setEmail("old@hotel.com");

       var updateRequest = new UpdateHotelRequest();
       updateRequest.setPassword("newPassword123");
       updateRequest.setBoostPackageLimit("10");
       updateRequest.setSuperDealLimit("5");

       Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
       Mockito.when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> invocation.getArgument(0));

       assertDoesNotThrow(() -> hotelService.updateHotel(hotelId, updateRequest));
   }

   @Test
   @DisplayName("Delete hotel - Hotel found")
   public void testDeleteHotel() {
       Long hotelId = 1L;
       var hotel = new Hotel();
       hotel.setId(hotelId);

       Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
       Mockito.doNothing().when(hotelRepository).deleteById(hotelId);

       assertDoesNotThrow(() -> hotelService.deleteHotel(hotelId));
   }

   @Test
   @DisplayName("Delete hotel - Hotel not found")
   public void testDeleteHotel_NotFound() {
       Long hotelId = 1L;

       Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

       assertThrows(HotelNotFoundException.class, () -> hotelService.deleteHotel(hotelId));
   }

   @Test
   @DisplayName("Find hotels by status")
   public void testFindHotelsByStatus() {
       Boolean status = true;
       var hotel1 = new Hotel();
       hotel1.setId(1L);
       hotel1.setStatus(status);

       var hotel2 = new Hotel();
       hotel2.setId(2L);
       hotel2.setStatus(status);

       List<Hotel> hotels = List.of(hotel1, hotel2);

       Mockito.when(hotelRepository.findByStatus(status)).thenReturn(hotels);

       var foundHotels = hotelService.findByStatus(status);

       assertEquals(2, foundHotels.size());
   }
}
