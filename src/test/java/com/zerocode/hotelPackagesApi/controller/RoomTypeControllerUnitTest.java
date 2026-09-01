package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.dto.RoomTypeDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotFoundException;
import com.zerocode.hotelPackagesApi.model.RoomType;
import com.zerocode.hotelPackagesApi.service.RoomTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoomTypeControllerUnitTest {

    @Mock
    private RoomTypeService roomTypeService;

    @InjectMocks
    private RoomTypeController roomTypeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomTypeController)
                .setControllerAdvice(new com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createRoomType_Success() throws Exception {
        // Given
        Long hotelId = 1L;
        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setType("Deluxe");

        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 150.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        doNothing().when(roomTypeService).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomTypeService, times(1)).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));
    }

    @Test
    void createRoomType_HotelNotFoundException() throws Exception {
        // Given
        Long hotelId = 999L;
        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setType("Deluxe");

        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 150.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        doThrow(new HotelNotFoundException("Hotel not found")).when(roomTypeService).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(roomTypeService, times(1)).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));
    }

    @Test
    void createRoomType_RoomTypeNotFoundException() throws Exception {
        // Given
        Long hotelId = 1L;
        RoomType roomType = new RoomType();
        roomType.setId(999L);
        roomType.setType("Invalid");

        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 150.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        doThrow(new RoomTypeNotFoundException("Room type not found")).when(roomTypeService).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(roomTypeService, times(1)).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));
    }

    @Test
    void createRoomType_RoomTypeNotCreatedException() throws Exception {
        // Given
        Long hotelId = 1L;
        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setType("Deluxe");

        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(roomType, 150.0);
        CreateRoomTypeRequest request = new CreateRoomTypeRequest(Arrays.asList(roomTypeDTO));

        doThrow(new RoomTypeNotCreatedException("Failed to create room type")).when(roomTypeService).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(roomTypeService, times(1)).createRoomType(eq(hotelId), any(CreateRoomTypeRequest.class));
    }

    @Test
    void updateRoomTypePrice_Success() throws Exception {
        // Given
        Long hotelId = 1L;
        Long roomTypeId = 1L;
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(200.0);

        doNothing().when(roomTypeService).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomTypeService, times(1)).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));
    }

    @Test
    void updateRoomTypePrice_HotelNotFoundException() throws Exception {
        // Given
        Long hotelId = 999L;
        Long roomTypeId = 1L;
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(200.0);

        doThrow(new HotelNotFoundException("Hotel not found")).when(roomTypeService).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(roomTypeService, times(1)).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));
    }

    @Test
    void updateRoomTypePrice_RoomTypeNotCreatedException() throws Exception {
        // Given
        Long hotelId = 1L;
        Long roomTypeId = 999L;
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(200.0);

        doThrow(new RoomTypeNotCreatedException("Room type not found")).when(roomTypeService).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(roomTypeService, times(1)).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));
    }

    @Test
    void deleteRoomTypeFromHotel_Success() throws Exception {
        // Given
        Long hotelId = 1L;
        Long roomTypeId = 1L;

        doNothing().when(roomTypeService).deleteRoomTypeFromHotel(eq(hotelId), eq(roomTypeId));

        // When & Then
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId))
                .andExpect(status().isOk());

        verify(roomTypeService, times(1)).deleteRoomTypeFromHotel(eq(hotelId), eq(roomTypeId));
    }

    @Test
    void deleteRoomTypeFromHotel_HotelNotFoundException() throws Exception {
        // Given
        Long hotelId = 999L;
        Long roomTypeId = 1L;

        doThrow(new HotelNotFoundException("Hotel not found")).when(roomTypeService).deleteRoomTypeFromHotel(eq(hotelId), eq(roomTypeId));

        // When & Then
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId))
                .andExpect(status().isNotFound());

        verify(roomTypeService, times(1)).deleteRoomTypeFromHotel(eq(hotelId), eq(roomTypeId));
    }

    @Test
    void deleteRoomTypeFromHotel_RoomTypeNotCreatedException() throws Exception {
        // Given
        Long hotelId = 1L;
        Long roomTypeId = 999L;

        doThrow(new RoomTypeNotCreatedException("Room type not found")).when(roomTypeService).deleteRoomTypeFromHotel(eq(hotelId), eq(roomTypeId));

        // When & Then
        mockMvc.perform(delete("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId))
                .andExpect(status().isBadRequest());

        verify(roomTypeService, times(1)).deleteRoomTypeFromHotel(eq(hotelId), eq(roomTypeId));
    }

    @Test
    void getAllRoomTypesByHotel_Success() throws Exception {
        // Given
        Long hotelId = 1L;
        
        RoomType roomType1 = new RoomType();
        roomType1.setId(1L);
        roomType1.setType("Deluxe");
        
        RoomType roomType2 = new RoomType();
        roomType2.setId(2L);
        roomType2.setType("Standard");

        List<RoomTypeDTO> expectedRoomTypes = Arrays.asList(
                new RoomTypeDTO(roomType1, 150.0),
                new RoomTypeDTO(roomType2, 100.0)
        );

        when(roomTypeService.getAllRoomTypesByHotelId(hotelId)).thenReturn(expectedRoomTypes);

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", hotelId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].roomType.id").value(1))
                .andExpect(jsonPath("$[0].roomType.type").value("Deluxe"))
                .andExpect(jsonPath("$[0].price").value(150.0))
                .andExpect(jsonPath("$[1].roomType.id").value(2))
                .andExpect(jsonPath("$[1].roomType.type").value("Standard"))
                .andExpect(jsonPath("$[1].price").value(100.0));

        verify(roomTypeService, times(1)).getAllRoomTypesByHotelId(hotelId);
    }

    @Test
    void getAllRoomTypesByHotel_EmptyList() throws Exception {
        // Given
        Long hotelId = 1L;
        List<RoomTypeDTO> expectedRoomTypes = Arrays.asList();

        when(roomTypeService.getAllRoomTypesByHotelId(hotelId)).thenReturn(expectedRoomTypes);

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", hotelId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(roomTypeService, times(1)).getAllRoomTypesByHotelId(hotelId);
    }

    @Test
    void getAllRoomTypesByHotel_HotelNotFoundException() throws Exception {
        // Given
        Long hotelId = 999L;

        when(roomTypeService.getAllRoomTypesByHotelId(hotelId)).thenThrow(new HotelNotFoundException("Hotel not found"));

        // When & Then
        mockMvc.perform(get("/hotels/{hotel-id}/room-types", hotelId))
                .andExpect(status().isNotFound());

        verify(roomTypeService, times(1)).getAllRoomTypesByHotelId(hotelId);
    }

    @Test
    void createRoomType_InvalidRequest() throws Exception {
        // Given
        Long hotelId = 1L;
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(roomTypeService, never()).createRoomType(any(), any());
    }

    @Test
    void updateRoomTypePrice_InvalidRequest() throws Exception {
        // Given
        Long hotelId = 1L;
        Long roomTypeId = 1L;
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(roomTypeService, never()).updateRoomTypePrice(any(), any(), any());
    }

    @Test
    void createRoomType_NullRequest() throws Exception {
        // Given
        Long hotelId = 1L;

        // When & Then
        mockMvc.perform(post("/hotels/{hotel-id}/room-types", hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());

        verify(roomTypeService, never()).createRoomType(any(), any());
    }

    @Test
    void updateRoomTypePrice_NullPrice() throws Exception {
        // Given
        Long hotelId = 1L;
        Long roomTypeId = 1L;
        UpdateRoomTypeRequest request = new UpdateRoomTypeRequest();
        request.setPrice(null);

        doNothing().when(roomTypeService).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));

        // When & Then
        mockMvc.perform(put("/hotels/{hotel-id}/room-types/{room-type-id}", hotelId, roomTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomTypeService, times(1)).updateRoomTypePrice(eq(hotelId), eq(roomTypeId), any(UpdateRoomTypeRequest.class));
    }
} 