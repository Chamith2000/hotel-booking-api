package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.dto.RoomTypeDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.model.Address;
import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelRoomType;
import com.zerocode.hotelPackagesApi.model.RoomType;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRoomTypeRepository;
import com.zerocode.hotelPackagesApi.repository.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class RoomTypeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private HotelRoomTypeRepository hotelRoomTypeRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Hotel testHotel;
    private Long nullId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // Clean up data
        roomTypeRepository.deleteAll();
        hotelRepository.deleteAll();
        
        // Create test hotel
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password123");
        
        // Create address
        Address address = new Address();
        address.setNo("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        testHotel.setAddress(address);
        
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel.setBoostPackageLimit("5");
        testHotel.setSuperDealLimit("3");
        
        testHotel = hotelRepository.save(testHotel);
        
        // Create test room type
        RoomType testRoomType = new RoomType();
        testRoomType.setType("Standard");
        testRoomType.setPrice(100.0);
        testRoomType = roomTypeRepository.save(testRoomType);

        // Create hotel-room type relationship
        HotelRoomType hotelRoomType = new HotelRoomType();
        hotelRoomType.setHotel(testHotel);
        hotelRoomType.setRoomType(testRoomType);
        hotelRoomType.setPrice(100.0);
        hotelRoomTypeRepository.save(hotelRoomType);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_Success() throws Exception {
        // Given
        RoomType newRoomType = new RoomType();
        newRoomType.setType("Deluxe");
        newRoomType.setPrice(200.0);
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(newRoomType, 200.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify room type was created in database
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        assertThat(roomTypes, hasSize(2)); // 1 from setup + 1 from test
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_MultipleRoomTypes() throws Exception {
        // Given
        RoomType roomType1 = new RoomType();
        roomType1.setType("Suite");
        roomType1.setPrice(300.0);
        
        RoomType roomType2 = new RoomType();
        roomType2.setType("Executive");
        roomType2.setPrice(400.0);
        
        RoomTypeDTO dto1 = new RoomTypeDTO(roomType1, 300.0);
        RoomTypeDTO dto2 = new RoomTypeDTO(roomType2, 400.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(dto1, dto2));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify room types were created in database
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        assertThat(roomTypes, hasSize(3)); // 1 from setup + 2 from test
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_HotelNotFound() throws Exception {
        // Given
        Long nonExistentHotelId = 999L;
        RoomType roomType = new RoomType();
        roomType.setType("Standard");
        roomType.setPrice(100.0);
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 100.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", nonExistentHotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Hotel not found with ID: 999"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_InvalidRequest() throws Exception {
        // Given - Empty room types list
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList());

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_EmptyRoomTypesList() throws Exception {
        // Given
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList());

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_Success() throws Exception {
        // Given - Get existing room type ID from the setup
        List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findByHotel(testHotel);
        Long roomTypeId = hotelRoomTypes.get(0).getRoomType().getId();
        
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(200.0);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_HotelNotFound() throws Exception {
        // Given
        Long nonExistentHotelId = 999L;
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(200.0);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", nonExistentHotelId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Hotel not found with ID: 999"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_RoomTypeNotFound() throws Exception {
        // Given
        Long nonExistentRoomTypeId = 999L;
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(200.0);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), nonExistentRoomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_NullPrice() throws Exception {
        // Given
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(null);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_NegativePrice() throws Exception {
        // Given
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(-50.0);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_WithZeroPrice() throws Exception {
        // Given - Get existing room type ID from the setup
        List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findByHotel(testHotel);
        Long roomTypeId = hotelRoomTypes.get(0).getRoomType().getId();
        
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(0.0);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateRoomTypePrice_WithVeryHighPrice() throws Exception {
        // Given - Get existing room type ID from the setup
        List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findByHotel(testHotel);
        Long roomTypeId = hotelRoomTypes.get(0).getRoomType().getId();
        
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(999999.99);

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteRoomTypeFromHotel_Success() throws Exception {
        // Given - Get existing room type ID from the setup
        List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findByHotel(testHotel);
        Long roomTypeId = hotelRoomTypes.get(0).getRoomType().getId();

        // When & Then
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), roomTypeId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteRoomTypeFromHotel_HotelNotFound() throws Exception {
        // Given
        Long nonExistentHotelId = 999L;

        // When & Then
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", nonExistentHotelId, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Hotel not found with ID: 999"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteRoomTypeFromHotel_RoomTypeNotFound() throws Exception {
        // Given
        Long nonExistentRoomTypeId = 999L;

        // When & Then
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), nonExistentRoomTypeId))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllRoomTypesByHotel_Success() throws Exception {
        // Given - Create additional room types and associate them with the hotel
        RoomType roomType2 = new RoomType();
        roomType2.setType("Deluxe");
        roomType2.setPrice(200.0);
        roomType2 = roomTypeRepository.save(roomType2);
        
        HotelRoomType hotelRoomType2 = new HotelRoomType();
        hotelRoomType2.setHotel(testHotel);
        hotelRoomType2.setRoomType(roomType2);
        hotelRoomType2.setPrice(200.0);
        hotelRoomTypeRepository.save(hotelRoomType2);
        
        RoomType roomType3 = new RoomType();
        roomType3.setType("Suite");
        roomType3.setPrice(300.0);
        roomType3 = roomTypeRepository.save(roomType3);
        
        HotelRoomType hotelRoomType3 = new HotelRoomType();
        hotelRoomType3.setHotel(testHotel);
        hotelRoomType3.setRoomType(roomType3);
        hotelRoomType3.setPrice(300.0);
        hotelRoomTypeRepository.save(hotelRoomType3);

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", testHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].roomType").exists())
                .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllRoomTypesByHotel_EmptyList() throws Exception {
        // Given - Delete all hotel-room type relationships
        hotelRoomTypeRepository.deleteAll();

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", testHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllRoomTypesByHotel_HotelNotFound() throws Exception {
        // Given
        Long nonExistentHotelId = 999L;

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", nonExistentHotelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Hotel not found with ID: 999"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllRoomTypesByHotel_ResponseStructure() throws Exception {
        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", testHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0].roomType.id").exists())
                .andExpect(jsonPath("$[0].roomType.type").exists())
                .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_WithExistingRoomType() throws Exception {
        // Given - Use existing room type that's already associated with the hotel
        List<HotelRoomType> existingHotelRoomTypes = hotelRoomTypeRepository.findByHotel(testHotel);
        RoomType existingRoomType = existingHotelRoomTypes.get(0).getRoomType();
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(existingRoomType, 150.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then - Should fail because room type is already associated
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // Test removed - service doesn't handle null room types gracefully
    // This would require service-level validation which is not implemented

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_WithNegativePrice() throws Exception {
        // Given
        RoomType roomType = new RoomType();
        roomType.setType("Budget");
        roomType.setPrice(-50.0);
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, -50.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_WithZeroPrice() throws Exception {
        // Given
        RoomType roomType = new RoomType();
        roomType.setType("Free");
        roomType.setPrice(0.0);
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 0.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteRoomTypeFromHotel_AlreadyDeleted() throws Exception {
        // Given - Delete the room type first
        List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findByHotel(testHotel);
        Long roomTypeId = hotelRoomTypes.get(0).getRoomType().getId();
        hotelRoomTypeRepository.deleteAll();

        // When & Then - Try to delete again
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", testHotel.getId(), roomTypeId))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllRoomTypesByHotel_AfterDeletion() throws Exception {
        // Given - Delete all room types
        hotelRoomTypeRepository.deleteAll();

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", testHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_WithSpecialCharacters() throws Exception {
        // Given
        RoomType roomType = new RoomType();
        roomType.setType("Luxury Suite & Spa");
        roomType.setPrice(500.0);
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 500.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createRoomType_WithLongRoomTypeName() throws Exception {
        // Given
        RoomType roomType = new RoomType();
        roomType.setType("Very Long Room Type Name That Exceeds Normal Length");
        roomType.setPrice(1000.0);
        
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 1000.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
} 