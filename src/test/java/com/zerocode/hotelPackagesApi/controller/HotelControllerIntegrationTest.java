package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private CreateHotelRequest createHotelRequest;

    @BeforeEach
    void setUp() {
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password123");

        Address address = new Address();
        address.setNo("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        testHotel.setAddress(address);

        HotelContactNumber contactNumber = new HotelContactNumber();
        contactNumber.setNumber("+1234567890");
        contactNumber.setHotel(testHotel);
        
        ArrayList<HotelContactNumber> contactNumbers = new ArrayList<>();
        contactNumbers.add(contactNumber);
        testHotel.setContactNumbers(contactNumbers);

        HotelImage hotelImage = new HotelImage();
        hotelImage.setUrl("http://example.com/hotel.jpg");
        hotelImage.setHotel(testHotel);
        
        ArrayList<HotelImage> hotelImages = new ArrayList<>();
        hotelImages.add(hotelImage);
        testHotel.setHotelPhotos(hotelImages);

        testHotel.setLogo("http://example.com/logo.png");
        testHotel.setBoostPackageLimit("5");
        testHotel.setSuperDealLimit("3");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.PENDING);
        testHotel = hotelRepository.save(testHotel);

        createHotelRequest = new CreateHotelRequest();
        createHotelRequest.setName("New Hotel");
        createHotelRequest.setEmail("new@hotel.com");
        createHotelRequest.setPassword("newpassword123");

        Address newAddress = new Address();
        newAddress.setNo("456");
        newAddress.setStreet("New Street");
        newAddress.setCity("New City");
        newAddress.setProvince("New Province");
        createHotelRequest.setAddress(newAddress);

        // Use DTOs instead of model objects
        HotelContactNumberDTO contactNumberDTO = new HotelContactNumberDTO(null, "+1234567890");
        HotelImageDTO hotelImageDTO = new HotelImageDTO(null, "http://example.com/new-hotel.jpg");
        
        createHotelRequest.setContactNumbers(Arrays.asList(contactNumberDTO));
        createHotelRequest.setLogo("http://example.com/new-logo.png");
        createHotelRequest.setHotelPhotos(Arrays.asList(hotelImageDTO));
        createHotelRequest.setBoostPackageLimit("10");
        createHotelRequest.setSuperDealLimit("5");
        createHotelRequest.setStatus(true);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createHotel_Success() throws Exception {
        int initialCount = (int) hotelRepository.count();

        mockMvc.perform(post("/hotels")
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createHotelRequest)))
                .andExpect(status().isOk());

        assertEquals(initialCount + 1, hotelRepository.count());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllHotels_Success() throws Exception {
        mockMvc.perform(get("/hotels")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelList", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getHotelById_Success() throws Exception {
        mockMvc.perform(get("/hotels/{hotel-id}", testHotel.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelListItem.name", is("Test Hotel")));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateHotel_Success() throws Exception {
        UpdateHotelRequest updateRequest = new UpdateHotelRequest();
        updateRequest.setPassword("newpassword456");

        mockMvc.perform(patch("/hotels/{hotel-id}", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteHotel_Success() throws Exception {
        Long hotelId = testHotel.getId();

        mockMvc.perform(delete("/hotels/{hotel-id}", hotelId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        assertFalse(hotelRepository.existsById(hotelId));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void filterByStatus_Active() throws Exception {
        mockMvc.perform(get("/hotels/filterByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelList", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingHotels_Success() throws Exception {
        mockMvc.perform(get("/pending")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void approveHotel_Success() throws Exception {
        mockMvc.perform(put("/{hotelId}/approve", testHotel.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hotel Approved Successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectHotel_Success() throws Exception {
        mockMvc.perform(put("/{hotelId}/reject", testHotel.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hotel Rejected Successfully"));
    }
}
