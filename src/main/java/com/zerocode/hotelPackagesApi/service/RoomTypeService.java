package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.dto.RoomTypeDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotFoundException;

import java.util.List;

public interface RoomTypeService{
    void createRoomType(Long hotelId, CreateRoomTypeRequest createRoomTypeRequest) throws HotelNotFoundException, RoomTypeNotFoundException, RoomTypeNotCreatedException;
    void updateRoomTypePrice(Long hotelId, Long roomTypeId, UpdateRoomTypeRequest updateRoomTypeRequest) throws HotelNotFoundException, RoomTypeNotCreatedException;
    void deleteRoomTypeFromHotel(Long hotelId, Long roomTypeId) throws HotelNotFoundException, RoomTypeNotCreatedException;
    List<RoomTypeDTO> getAllRoomTypesByHotelId(Long hotelId) throws HotelNotFoundException;
}
