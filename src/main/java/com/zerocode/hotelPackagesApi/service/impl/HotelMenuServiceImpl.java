package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotFoundException;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelMenu;
import com.zerocode.hotelPackagesApi.model.MenuItem;
import com.zerocode.hotelPackagesApi.repository.HotelMenuRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.service.HotelMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HotelMenuServiceImpl implements HotelMenuService {
    private HotelMenuRepository hotelMenuRepository;
    private HotelRepository hotelRepository;

    @Override
    public void createHotelMenu(Long hotelId, CreateHotelMenuRequest createHotelMenuRequest) throws HotelMenuNotCreatedException{
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()->new HotelMenuNotCreatedException("Hotel not found with Id" + hotelId));

        // Check for duplicate menu type for the same hotel
        if (hotelMenuRepository.existsByHotel_IdAndType(hotelId, createHotelMenuRequest.getType())) {
            throw new HotelMenuNotCreatedException("Menu of this type already exists for this hotel");
        }

        HotelMenu hotelMenu = new HotelMenu();
        hotelMenu.setType(createHotelMenuRequest.getType());
        hotelMenu.setHotel(hotel);

        List<MenuItem>menuItems = createHotelMenuRequest.getMenuItems();
        menuItems.forEach(menuItem -> menuItem.setHotelMenu(hotelMenu));
        hotelMenu.setMenuItems(menuItems);

        hotelMenuRepository.save(hotelMenu);

    }

    @Override
    public List<HotelMenu> getMenuByHotelId(Long hotelId){
        return hotelMenuRepository.findByHotel_Id(hotelId);
    }

    @Override
    public void update(Long menuId, UpdateHotelMenuRequest updateHotelMenuRequest)throws HotelMenuNotFoundException{
        HotelMenu hotelMenu = hotelMenuRepository.findById(menuId)
                .orElseThrow(()-> new HotelMenuNotFoundException("Menu not found with Id" + menuId));

        hotelMenu.setType(hotelMenu.getType());

        List<MenuItem> menuItems = updateHotelMenuRequest.getMenuItems();
        menuItems.forEach(menuItem -> menuItem.setHotelMenu(hotelMenu));
        hotelMenu.setMenuItems(menuItems);
        hotelMenuRepository.save(hotelMenu);

    }

    @Override
    public void delete(Long menuId)throws HotelMenuNotFoundException{
        HotelMenu hotelMenu = hotelMenuRepository.findById(menuId)
                .orElseThrow(()->new HotelMenuNotFoundException("Menu not found with Id" +menuId));
        hotelMenuRepository.delete(hotelMenu);
    }




}
