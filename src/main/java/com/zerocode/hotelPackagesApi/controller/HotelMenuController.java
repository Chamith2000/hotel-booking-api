package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.response.HotelMenuListResponse;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotFoundException;
import com.zerocode.hotelPackagesApi.model.HotelMenu;
import com.zerocode.hotelPackagesApi.service.HotelMenuService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@AllArgsConstructor
public class HotelMenuController {
    @Autowired
    private HotelMenuService hotelMenuService;

    @RolesAllowed({"HOTEL"})
    @PostMapping(value = "/hotels/{hotel-id}/menus", headers = "X-Api-Version=v1")
    public void create(@PathVariable("hotel-id") Long hotelId, @RequestBody @Valid CreateHotelMenuRequest createHotelMenuRequest) throws HotelMenuNotCreatedException {
        hotelMenuService.createHotelMenu(hotelId, createHotelMenuRequest);
    }

    @RolesAllowed({"HOTEL"})
    @PutMapping(value = "/menus/{menu-id}", headers = "X-Api-Version=v1")
    public void update(@PathVariable("menu-id") Long menuId, @RequestBody UpdateHotelMenuRequest updateHotelMenuRequest) throws HotelMenuNotFoundException {
        hotelMenuService.update(menuId, updateHotelMenuRequest);
    }

    @RolesAllowed({"HOTEL", "CUSTOMER", "ADMIN"})
    @GetMapping("hotels/{hotelId}/menus")
    public HotelMenuListResponse getMenuByHotelId(@PathVariable("hotelId") Long hotelId){
        List<HotelMenu> hotelMenus = hotelMenuService.getMenuByHotelId(hotelId);
        return new HotelMenuListResponse(hotelMenus);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @DeleteMapping(value = "/menus/{menu-id}", headers = "X-Api-Version=v1")
    public void delete(@PathVariable("menu-id") Long menuId)throws HotelMenuNotFoundException {
        hotelMenuService.delete(menuId);

    }

}
