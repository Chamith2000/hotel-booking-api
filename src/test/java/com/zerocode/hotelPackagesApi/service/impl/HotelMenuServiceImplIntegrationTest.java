package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotFoundException;
import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelMenu;
import com.zerocode.hotelPackagesApi.model.MenuItem;
import com.zerocode.hotelPackagesApi.repository.HotelMenuRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class HotelMenuServiceImplIntegrationTest {

    @Autowired
    private HotelMenuServiceImpl hotelMenuService;

    @Autowired
    private HotelMenuRepository hotelMenuRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;

    @BeforeEach
    void setup() {
        hotelMenuRepository.deleteAll();
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.saveAndFlush(testHotel);
    }

    @Test
    @DisplayName("Create hotel menu - Success")
    void testCreateHotelMenuSuccess() throws Exception {
        MenuItem item1 = new MenuItem();
        item1.setItemName("Pancakes");
        item1.setPrice(5.0);
        MenuItem item2 = new MenuItem();
        item2.setItemName("Hoppers");
        item2.setPrice(3.0);
        List<MenuItem> menuItems = new ArrayList<>(Arrays.asList(item1, item2));

        CreateHotelMenuRequest request = new CreateHotelMenuRequest();
        request.setType("Breakfast");
        request.setMenuItems(menuItems);

        hotelMenuService.createHotelMenu(testHotel.getId(), request);

        List<HotelMenu> menus = hotelMenuRepository.findByHotel_Id(testHotel.getId());
        assertEquals(1, menus.size());
        HotelMenu menu = menus.get(0);
        assertEquals("Breakfast", menu.getType());
        assertEquals(2, menu.getMenuItems().size());
        assertTrue(menu.getMenuItems().stream().anyMatch(mi -> mi.getItemName().equals("Pancakes")));
        assertTrue(menu.getMenuItems().stream().anyMatch(mi -> mi.getItemName().equals("Hoppers")));
    }

    @Test
    @DisplayName("Create hotel menu - Hotel Not Found")
    void testCreateHotelMenuHotelNotFound() {
        CreateHotelMenuRequest request = new CreateHotelMenuRequest();
        request.setType("Lunch");
        request.setMenuItems(Collections.emptyList());
        assertThrows(HotelMenuNotCreatedException.class, () ->
                hotelMenuService.createHotelMenu(999L, request));
    }

    @Test
    @DisplayName("Create hotel menu - Duplicate Type")
    void testCreateHotelMenuDuplicateType() throws Exception {
        MenuItem item = new MenuItem();
        item.setItemName("Burger");
        item.setPrice(10.0);
        CreateHotelMenuRequest request = new CreateHotelMenuRequest();
        request.setType("Lunch");
        request.setMenuItems(new ArrayList<>(Collections.singletonList(item)));
        hotelMenuService.createHotelMenu(testHotel.getId(), request);
        // Try to create again with same type
        assertThrows(HotelMenuNotCreatedException.class, () ->
                hotelMenuService.createHotelMenu(testHotel.getId(), request));
    }

    @Test
    @DisplayName("Get menus by hotel ID - Success")
    void testGetMenusByHotelIdSuccess() throws Exception {
        MenuItem item = new MenuItem();
        item.setItemName("Pizza");
        item.setPrice(8.0);
        CreateHotelMenuRequest request = new CreateHotelMenuRequest();
        request.setType("Dinner");
        request.setMenuItems(new ArrayList<>(Collections.singletonList(item)));
        hotelMenuService.createHotelMenu(testHotel.getId(), request);

        List<HotelMenu> menus = hotelMenuService.getMenuByHotelId(testHotel.getId());
        assertEquals(1, menus.size());
        assertEquals("Dinner", menus.get(0).getType());
        assertEquals(1, menus.get(0).getMenuItems().size());
        assertEquals("Pizza", menus.get(0).getMenuItems().get(0).getItemName());
    }

    @Test
    @DisplayName("Update hotel menu - Success")
    void testUpdateHotelMenuSuccess() throws Exception {
        // Create initial menu
        MenuItem item = new MenuItem();
        item.setItemName("Rice");
        item.setPrice(6.0);
        CreateHotelMenuRequest createRequest = new CreateHotelMenuRequest();
        createRequest.setType("Lunch");
        createRequest.setMenuItems(new ArrayList<>(Collections.singletonList(item)));
        hotelMenuService.createHotelMenu(testHotel.getId(), createRequest);
        HotelMenu menu = hotelMenuRepository.findByHotel_Id(testHotel.getId()).get(0);

        // Update menu items
        MenuItem newItem = new MenuItem();
        newItem.setItemName("Noodles");
        newItem.setPrice(7.0);
        UpdateHotelMenuRequest updateRequest = new UpdateHotelMenuRequest();
        updateRequest.setMenuItems(new ArrayList<>(Collections.singletonList(newItem)));
        updateRequest.setType("Lunch");
        hotelMenuService.update(menu.getId(), updateRequest);

        HotelMenu updatedMenu = hotelMenuRepository.findById(menu.getId()).get();
        assertEquals(1, updatedMenu.getMenuItems().size());
        assertEquals("Noodles", updatedMenu.getMenuItems().get(0).getItemName());
    }

    @Test
    @DisplayName("Update hotel menu - Not Found")
    void testUpdateHotelMenuNotFound() {
        UpdateHotelMenuRequest updateRequest = new UpdateHotelMenuRequest();
        updateRequest.setMenuItems(Collections.emptyList());
        updateRequest.setType("Lunch");
        assertThrows(HotelMenuNotFoundException.class, () ->
                hotelMenuService.update(999L, updateRequest));
    }

    @Test
    @DisplayName("Delete hotel menu - Success")
    void testDeleteHotelMenuSuccess() throws Exception {
        MenuItem item = new MenuItem();
        item.setItemName("Soup");
        item.setPrice(4.0);
        CreateHotelMenuRequest request = new CreateHotelMenuRequest();
        request.setType("Snacks");
        request.setMenuItems(new ArrayList<>(Collections.singletonList(item)));
        hotelMenuService.createHotelMenu(testHotel.getId(), request);
        HotelMenu menu = hotelMenuRepository.findByHotel_Id(testHotel.getId()).get(0);

        hotelMenuService.delete(menu.getId());
        assertTrue(hotelMenuRepository.findById(menu.getId()).isEmpty());
    }

    @Test
    @DisplayName("Delete hotel menu - Not Found")
    void testDeleteHotelMenuNotFound() {
        assertThrows(HotelMenuNotFoundException.class, () ->
                hotelMenuService.delete(999L));
    }
}
