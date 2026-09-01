package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotFoundException;
import com.zerocode.hotelPackagesApi.model.HotelMenu;

import java.util.List;

public interface HotelMenuService {
    void createHotelMenu(Long hotelId, CreateHotelMenuRequest createHotelMenuRequest) throws HotelMenuNotCreatedException;
    List<HotelMenu> getMenuByHotelId(Long hotelId);
    void update(Long hotelId, UpdateHotelMenuRequest updateHotelMenuRequest)throws HotelMenuNotFoundException;
    void delete(Long menuId)throws HotelMenuNotFoundException;
}
