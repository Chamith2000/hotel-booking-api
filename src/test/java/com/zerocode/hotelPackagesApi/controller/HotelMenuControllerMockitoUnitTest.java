package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelMenuRequest;
import com.zerocode.hotelPackagesApi.controller.response.HotelMenuListResponse;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelMenuNotFoundException;
import com.zerocode.hotelPackagesApi.model.HotelMenu;
import com.zerocode.hotelPackagesApi.model.MenuItem;
import com.zerocode.hotelPackagesApi.service.HotelMenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HotelMenuControllerMockitoUnitTest {

    @Mock
    private HotelMenuService hotelMenuService;

    @InjectMocks
    private HotelMenuController hotelMenuController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelMenuController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createHotelMenu_Success() throws Exception {
        CreateHotelMenuRequest req = new CreateHotelMenuRequest("Lunch", Collections.emptyList());
        mockMvc.perform(post("/hotels/{hotel-id}/menus", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        verify(hotelMenuService, times(1)).createHotelMenu(eq(1L), any(CreateHotelMenuRequest.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createHotelMenu_NotCreated() throws Exception {
        CreateHotelMenuRequest req = new CreateHotelMenuRequest("Lunch", Collections.emptyList());
        doThrow(new HotelMenuNotCreatedException("not created")).when(hotelMenuService).createHotelMenu(eq(1L), any(CreateHotelMenuRequest.class));
        mockMvc.perform(post("/hotels/{hotel-id}/menus", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateHotelMenu_Success() throws Exception {
        UpdateHotelMenuRequest req = new UpdateHotelMenuRequest("Dinner", Collections.emptyList());
        mockMvc.perform(put("/menus/{menu-id}", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        verify(hotelMenuService, times(1)).update(eq(1L), any(UpdateHotelMenuRequest.class));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updateHotelMenu_NotFound() throws Exception {
        UpdateHotelMenuRequest req = new UpdateHotelMenuRequest("Dinner", Collections.emptyList());
        doThrow(new HotelMenuNotFoundException("not found")).when(hotelMenuService).update(eq(99L), any(UpdateHotelMenuRequest.class));
        mockMvc.perform(put("/menus/{menu-id}", 99L)
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
    @WithMockUser(roles = {"HOTEL", "CUSTOMER", "ADMIN"})
    void getMenuByHotelId_Success() throws Exception {
        HotelMenu menu = new HotelMenu();
        menu.setId(1L);
        menu.setType("Lunch");
        menu.setMenuItems(Collections.emptyList());
        when(hotelMenuService.getMenuByHotelId(1L)).thenReturn(List.of(menu));
        mockMvc.perform(get("/hotels/{hotelId}/menus", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelMenuLists", hasSize(1)))
                .andExpect(jsonPath("$.hotelMenuLists[0].id").value(1L))
                .andExpect(jsonPath("$.hotelMenuLists[0].type").value("Lunch"));
    }

    @Test
    @WithMockUser(roles = {"HOTEL", "CUSTOMER", "ADMIN"})
    void getMenuByHotelId_Empty() throws Exception {
        when(hotelMenuService.getMenuByHotelId(1L)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/hotels/{hotelId}/menus", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelMenuLists", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteHotelMenu_Success() throws Exception {
        mockMvc.perform(delete("/menus/{menu-id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
        verify(hotelMenuService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteHotelMenu_NotFound() throws Exception {
        doThrow(new HotelMenuNotFoundException("not found")).when(hotelMenuService).delete(99L);
        mockMvc.perform(delete("/menus/{menu-id}", 99L)
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