package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.exception.HotelApprovalException;
import com.zerocode.hotelPackagesApi.exception.HotelNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.model.Hotel;

import java.util.List;

public interface HotelService {
    void createHotel(CreateHotelRequest dto) throws HotelNotCreatedException;
    List<Hotel> findAll();
    Hotel findById(Long hotelId) throws HotelNotFoundException;
    void updateHotel(Long hotelId, UpdateHotelRequest dto) throws HotelNotFoundException;
    void deleteHotel(Long hotelId) throws HotelNotFoundException;
    List<Hotel> findByStatus(Boolean status) throws HotelNotFoundException;
    List<Hotel> getPendingHotels();
    void approveHotel(Long hotelId) throws HotelNotFoundException, HotelApprovalException;
    void rejectHotel(Long hotelId) throws HotelNotFoundException, HotelApprovalException;
}
