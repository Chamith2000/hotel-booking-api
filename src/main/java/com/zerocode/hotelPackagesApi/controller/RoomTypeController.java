package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.dto.RoomTypeDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotFoundException;
import com.zerocode.hotelPackagesApi.service.RoomTypeService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class RoomTypeController {
    private RoomTypeService roomTypeService;

    @RolesAllowed({"HOTEL"})
    @PostMapping(value = "/hotels/{hotel-id}/room-types")
    public void create(@PathVariable("hotel-id") Long hotelId, @RequestBody CreateRoomTypeRequest createRoomTypeRequest) throws HotelNotFoundException, RoomTypeNotFoundException, RoomTypeNotCreatedException {
        roomTypeService.createRoomType(hotelId, createRoomTypeRequest);
    }

    @RolesAllowed({"HOTEL"})
    @PutMapping(value = "/hotels/{hotel-id}/room-types/{room-type-id}")
    public void updatePrice(@PathVariable("hotel-id") Long hotelId, @PathVariable("room-type-id") Long roomTypeId, @RequestBody UpdateRoomTypeRequest updateRoomTypeRequest) throws RoomTypeNotCreatedException, HotelNotFoundException {
        roomTypeService.updateRoomTypePrice(hotelId, roomTypeId, updateRoomTypeRequest);
    }

    @RolesAllowed({"HOTEL"})
    @DeleteMapping(value = "/hotels/{hotel-id}/room-types/{room-type-id}")
    public void deleteRoomTypeFromHotel(@PathVariable("hotel-id") Long hotelId, @PathVariable("room-type-id") Long roomTypeId) throws RoomTypeNotCreatedException, HotelNotFoundException {
            roomTypeService.deleteRoomTypeFromHotel(hotelId, roomTypeId);
    }

    @RolesAllowed({"HOTEL"})
    @GetMapping(value = "/hotels/{hotel-id}/room-types")
    public List<RoomTypeDTO> getAllRoomTypesByHotel(@PathVariable("hotel-id") Long hotelId) throws HotelNotFoundException {
        List<RoomTypeDTO> roomTypes = roomTypeService.getAllRoomTypesByHotelId(hotelId);
        return roomTypes;
    }
}

