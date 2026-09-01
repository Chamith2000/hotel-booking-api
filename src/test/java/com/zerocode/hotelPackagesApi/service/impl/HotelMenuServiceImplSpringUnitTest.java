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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HotelMenuServiceImplSpringUnitTest {

    @Mock
    private HotelMenuRepository hotelMenuRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelMenuServiceImpl hotelMenuServiceImpl;

    @Test
    public void testCreateHotelMenuWithDuplicateId()throws Exception{

        Long hotelId = 1L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);

        CreateHotelMenuRequest createHotelMenuRequest = new CreateHotelMenuRequest();
        createHotelMenuRequest.setType("Lunch");

        MenuItem menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        menuItem1.setItemName("Pancakes");

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        menuItem2.setItemName("Hoppers");

        List<MenuItem> menuItems = Arrays.asList(menuItem1,menuItem2);
        createHotelMenuRequest.setMenuItems(menuItems);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

        hotelMenuServiceImpl.createHotelMenu(hotelId, createHotelMenuRequest);

        verify(hotelRepository, times(1)).findById(hotelId);
        verify(hotelMenuRepository, times(1)).save(any(HotelMenu.class));


    }

    @Test
    public void testCreateHotelMenu_HotelNotFound()throws Exception{

        Long hotelId = 1L;
        CreateHotelMenuRequest createHotelMenuRequest = new CreateHotelMenuRequest();
        createHotelMenuRequest.setType("Dinner");

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        assertThrows(HotelMenuNotCreatedException.class,()->hotelMenuServiceImpl.createHotelMenu(hotelId,createHotelMenuRequest));

        verify(hotelRepository,times(1)).findById(hotelId);
        verify(hotelMenuRepository,never()).save(any(HotelMenu.class));
    }

    @Test
    public void testGetMenuByHotelId ()throws Exception{
        Long hotelId = 1L;

        HotelMenu menu1 = new HotelMenu();
        menu1.setId(12L);
        menu1.setType("Breakfast");

        HotelMenu menu2 = new HotelMenu();
        menu2.setId(14L);
        menu2.setType("Dinner");

        List<HotelMenu> hotelMenus = Arrays.asList(menu1,menu2);

        when(hotelMenuRepository.findByHotel_Id(hotelId)).thenReturn(hotelMenus);

        List<HotelMenu> menuList = hotelMenuServiceImpl.getMenuByHotelId(hotelId);

        assertEquals(2,menuList.size());
        assertEquals("Breakfast",menuList.get(0).getType());
        assertEquals("Dinner",menuList.get(1).getType());

        verify(hotelMenuRepository,times(1)).findByHotel_Id(hotelId);

    }

    @Test
    public void testUpdateHotelMenu_Success()throws Exception{

        Long menuId = 1L;
        HotelMenu hotelMenu = new HotelMenu();
        hotelMenu.setId(menuId);
        hotelMenu.setType("Lunch");

        MenuItem menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        menuItem1.setItemName("Burger");

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        menuItem2.setItemName("Pasta");

        UpdateHotelMenuRequest updateHotelMenuRequest = new UpdateHotelMenuRequest();
        updateHotelMenuRequest.setMenuItems(Arrays.asList(menuItem1,menuItem2));

        when(hotelMenuRepository.findById(menuId)).thenReturn(Optional.of(hotelMenu));

        hotelMenuServiceImpl.update(menuId,updateHotelMenuRequest);

        verify(hotelMenuRepository,times(1)).findById(menuId);
        verify(hotelMenuRepository,times(1)).save(hotelMenu);

    }

    @Test
    public void testUpdateHotelMenu_MenuNotFound()throws Exception{

        Long menuId = 99L;
        UpdateHotelMenuRequest updateHotelMenuRequest = new UpdateHotelMenuRequest();

        when(hotelMenuRepository.findById(menuId)).thenReturn(Optional.empty());

        assertThrows(HotelMenuNotFoundException.class,()->hotelMenuServiceImpl.update(menuId,updateHotelMenuRequest));

        verify(hotelMenuRepository,never()).save(any(HotelMenu.class));
    }

    @Test
    public void  testDeleteHotelMenu_Success()throws Exception{
        Long menuId = 1L;
        HotelMenu menu = new HotelMenu();
        menu.setId(menuId);
        menu.setType("Lunch");

        when(hotelMenuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        hotelMenuServiceImpl.delete(menuId);

        verify(hotelMenuRepository,times(1)).findById(menuId);
        verify(hotelMenuRepository, times(1)).delete(menu);
    }

    @Test
    public void testDeleteHotelMenu_MenuNotFound()throws Exception{

        Long menuId = 1L;

        when(hotelMenuRepository.findById(menuId)).thenReturn(Optional.empty());

        assertThrows(HotelMenuNotFoundException.class,()-> hotelMenuServiceImpl.delete(menuId));

        verify(hotelMenuRepository,never()).delete(any(HotelMenu.class));
    }


}
