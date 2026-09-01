package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.exception.HotelNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.model.Address;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelContactNumber;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HotelServiceImplSpringIntegrationTest {

    @Autowired
    private HotelServiceImpl hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    public void setUp() {
        System.out.println("================================================================================================================================================================");
        hotelRepository.deleteAll();
    }

    @Test
    @DisplayName("Create hotel with duplicate email should throw exception")
    public void testCreateHotelWithDuplicateEmail() {
        CreateHotelRequest request = createSampleHotelRequest("test@hotel.com");

        // First creation should succeed
        assertDoesNotThrow(() -> hotelService.createHotel(request));

        // Second creation with same email should fail
        assertThrows(HotelNotCreatedException.class, () -> hotelService.createHotel(request));
    }

    @Test
    @DisplayName("Create hotel successfully")
    public void testCreateHotelSuccess() {
        CreateHotelRequest request = createSampleHotelRequest("test@hotel.com");

        assertDoesNotThrow(() -> hotelService.createHotel(request));

        List<Hotel> hotels = hotelService.findAll();
        assertEquals(1, hotels.size());
        Hotel createdHotel = hotels.get(0);
        assertEquals("Test Hotel", createdHotel.getName());
        assertEquals("test@hotel.com", createdHotel.getEmail());

        // Verify contact numbers
        List<HotelContactNumber> contactNumbers = createdHotel.getContactNumbers();
        assertEquals(1, contactNumbers.size());
        assertEquals("123-456-7890", contactNumbers.get(0).getNumber());
        assertNotNull(contactNumbers.get(0).getId());
        assertEquals(createdHotel, contactNumbers.get(0).getHotel());

        // Verify hotel photos
        assertEquals(1, createdHotel.getHotelPhotos().size());
        assertEquals("photo1.jpg", createdHotel.getHotelPhotos().get(0).getUrl());

        // Verify address
        Address address = createdHotel.getAddress();
        assertNotNull(address);
        assertEquals("123", address.getNo());
        assertEquals("Test St", address.getStreet());
        assertEquals("Test City", address.getCity());
        assertEquals("Test Province", address.getProvince());

        assertEquals("5", createdHotel.getBoostPackageLimit());
        assertEquals("3", createdHotel.getSuperDealLimit());
        assertTrue(createdHotel.getStatus());
    }

    @Test
    @DisplayName("Update hotel successfully")
    public void testUpdateHotelSuccess() throws HotelNotCreatedException, HotelNotFoundException {
        // Create hotel first
        CreateHotelRequest createRequest = createSampleHotelRequest("test@hotel.com");
        hotelService.createHotel(createRequest);
        List<Hotel> hotels = hotelService.findAll();
        assertEquals(1, hotels.size(), "Hotel should be created before update");
        Hotel createdHotel = hotels.get(0);

        // Update hotel
        UpdateHotelRequest updateRequest = new UpdateHotelRequest();
        updateRequest.setPassword("newpassword456");
        updateRequest.setBoostPackageLimit("10");
        updateRequest.setSuperDealLimit("5");
        updateRequest.setStatus(false);

        List<HotelContactNumberDTO> newContacts = new ArrayList<>();
        newContacts.add(new HotelContactNumberDTO(null, "987-654-3210")); // ID is null as it's a new contact
        updateRequest.setHotelContactNumberDTO(newContacts);

        List<HotelImageDTO> newPhotos = new ArrayList<>();
        newPhotos.add(new HotelImageDTO(null, "newphoto.jpg")); // ID is null as it's a new image
        updateRequest.setHotelImageDTO(newPhotos);

        assertDoesNotThrow(() -> hotelService.updateHotel(createdHotel.getId(), updateRequest));

        // Verify the hotel still exists after update
        Hotel updatedHotel = hotelService.findById(createdHotel.getId());
        assertNotNull(updatedHotel, "Hotel should exist after update");
        assertEquals("newpassword456", updatedHotel.getPassword());
        assertEquals("10", updatedHotel.getBoostPackageLimit());
        assertEquals("5", updatedHotel.getSuperDealLimit());
        assertFalse(updatedHotel.getStatus());

        // Verify updated contact numbers
        List<HotelContactNumber> updatedContacts = updatedHotel.getContactNumbers();
        assertEquals(1, updatedContacts.size());
        assertEquals("987-654-3210", updatedContacts.get(0).getNumber());
        assertNotNull(updatedContacts.get(0).getId());
        assertEquals(updatedHotel, updatedContacts.get(0).getHotel());

        // Verify updated photos
        assertEquals(1, updatedHotel.getHotelPhotos().size());
        assertEquals("newphoto.jpg", updatedHotel.getHotelPhotos().get(0).getUrl());
    }

    @Test
    @DisplayName("Delete hotel successfully")
    public void testDeleteHotelSuccess() throws HotelNotCreatedException {
        CreateHotelRequest request = createSampleHotelRequest("test@hotel.com");
        hotelService.createHotel(request);
        Hotel createdHotel = hotelService.findAll().get(0);

        assertDoesNotThrow(() -> hotelService.deleteHotel(createdHotel.getId()));
        assertEquals(0, hotelService.findAll().size());
    }

    @Test
    @DisplayName("Find hotels by status")
    public void testFindByStatus() throws HotelNotCreatedException {
        // Create active hotel
        CreateHotelRequest activeRequest = createSampleHotelRequest("active@hotel.com");
        activeRequest.setName("Active Hotel");
        hotelService.createHotel(activeRequest);

        // Create inactive hotel
        CreateHotelRequest inactiveRequest = createSampleHotelRequest("inactive@hotel.com");
        inactiveRequest.setName("Inactive Hotel");
        inactiveRequest.setStatus(false);
        hotelService.createHotel(inactiveRequest);

        List<Hotel> activeHotels = hotelService.findByStatus(true);
        List<Hotel> inactiveHotels = hotelService.findByStatus(false);

        assertEquals(1, activeHotels.size());
        assertEquals("Active Hotel", activeHotels.get(0).getName());
        assertEquals(1, inactiveHotels.size());
        assertEquals("Inactive Hotel", inactiveHotels.get(0).getName());
    }

    private CreateHotelRequest createSampleHotelRequest(String email) {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("Test Hotel");
        request.setEmail(email);
        request.setPassword("password123");

        Address address = new Address();
        address.setNo("123");
        address.setStreet("Test St");
        address.setCity("Test City");
        address.setProvince("Test Province");
        request.setAddress(address);

        request.setLogo("logo.png");

        List<HotelContactNumberDTO> contactNumbers = new ArrayList<>();
        contactNumbers.add(new HotelContactNumberDTO(null, "123-456-7890")); // ID is null as it's a new contact
        request.setContactNumbers(contactNumbers);

        List<HotelImageDTO> photos = new ArrayList<>();
        photos.add(new HotelImageDTO(null, "photo1.jpg")); // ID is null as it's a new image
        request.setHotelPhotos(photos);

        request.setBoostPackageLimit("5");
        request.setSuperDealLimit("3");
        request.setStatus(true);

        return request;
    }
}