package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelMenu;
import com.zerocode.hotelPackagesApi.model.MenuItem;
import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.repository.HotelMenuRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelMenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HotelMenuRepository hotelMenuRepository;
    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotelMenuRepository.deleteAll();
        hotelRepository.deleteAll();
        hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setApprovalStatus(ApprovalStatus.PENDING);
        hotel = hotelRepository.save(hotel);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createHotelMenu_Success() throws Exception {
        CreateHotelMenuRequest req = new CreateHotelMenuRequest("Lunch", Collections.emptyList());
        mockMvc.perform(post("/hotels/{hotel-id}/menus", hotel.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createHotelMenu_Duplicate() throws Exception {
        HotelMenu menu = new HotelMenu();
        menu.setType("Lunch");
        menu.setHotel(hotel);
        hotelMenuRepository.save(menu);
        CreateHotelMenuRequest req = new CreateHotelMenuRequest("Lunch", Collections.emptyList());
        mockMvc.perform(post("/hotels/{hotel-id}/menus", hotel.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createHotelMenu_Invalid() throws Exception {
        CreateHotelMenuRequest req = new CreateHotelMenuRequest("", null);
        mockMvc.perform(post("/hotels/{hotel-id}/menus", hotel.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateHotelMenu_Success() throws Exception {
        HotelMenu menu = new HotelMenu();
        menu.setType("Lunch");
        menu.setHotel(hotel);
        menu = hotelMenuRepository.save(menu);
        UpdateHotelMenuRequest req = new UpdateHotelMenuRequest("Dinner", Collections.emptyList());
        mockMvc.perform(put("/menus/{menu-id}", menu.getId())
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateHotelMenu_NotFound() throws Exception {
        UpdateHotelMenuRequest req = new UpdateHotelMenuRequest("Dinner", Collections.emptyList());
        mockMvc.perform(put("/menus/{menu-id}", 9999L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateHotelMenu_InvalidId() throws Exception {
        UpdateHotelMenuRequest req = new UpdateHotelMenuRequest("Dinner", Collections.emptyList());
        mockMvc.perform(put("/menus/{menu-id}", "invalid")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getMenuByHotelId_Empty() throws Exception {
        mockMvc.perform(get("/hotels/{hotelId}/menus", hotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelMenuLists", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getMenuByHotelId_NonEmpty() throws Exception {
        HotelMenu menu = new HotelMenu();
        menu.setType("Lunch");
        menu.setHotel(hotel);
        hotelMenuRepository.save(menu);
        mockMvc.perform(get("/hotels/{hotelId}/menus", hotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelMenuLists", hasSize(1)))
                .andExpect(jsonPath("$.hotelMenuLists[0].type").value("Lunch"));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteHotelMenu_Success() throws Exception {
        HotelMenu menu = new HotelMenu();
        menu.setType("Lunch");
        menu.setHotel(hotel);
        menu = hotelMenuRepository.save(menu);
        mockMvc.perform(delete("/menus/{menu-id}", menu.getId())
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteHotelMenu_NotFound() throws Exception {
        mockMvc.perform(delete("/menus/{menu-id}", 9999L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteHotelMenu_InvalidId() throws Exception {
        mockMvc.perform(delete("/menus/{menu-id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }
} 