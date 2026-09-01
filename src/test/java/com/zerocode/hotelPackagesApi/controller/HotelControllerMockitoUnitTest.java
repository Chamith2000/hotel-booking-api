package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.exception.HotelApprovalException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.HotelNotCreatedException;
import com.zerocode.hotelPackagesApi.model.Address;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelContactNumber;
import com.zerocode.hotelPackagesApi.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HotelControllerMockitoUnitTest {

    private MockMvc mockMvc;

    @Mock
    private HotelService hotelService;

    @InjectMocks
    private HotelController hotelController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        HandlerExceptionResolver customResolver = (request, response, handler, ex) -> {
            if (ex instanceof HotelNotFoundException) {
                response.setStatus(404);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                try {
                    response.getWriter().write(ex.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to write exception message to response", e);
                }
                return new ModelAndView();
            }
            if (ex instanceof HotelNotCreatedException) {
                response.setStatus(500);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                try {
                    response.getWriter().write(ex.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to write exception message to response", e);
                }
                return new ModelAndView();
            }
            if (ex instanceof org.springframework.web.servlet.NoHandlerFoundException) {
                response.setStatus(404);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                try {
                    org.springframework.web.servlet.NoHandlerFoundException nhfe = (org.springframework.web.servlet.NoHandlerFoundException) ex;
                    response.getWriter().write("No endpoint " + nhfe.getHttpMethod() + " " + nhfe.getRequestURL() + ".");
                } catch (Exception e) {
                    throw new RuntimeException("Failed to write exception message to response", e);
                }
                return new ModelAndView();
            }
            return null;
        };
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController)
                .setHandlerExceptionResolvers(customResolver)
                .build();
        objectMapper = new ObjectMapper();
        Mockito.reset(hotelService);
    }

    // CREATE HOTEL TESTS
    @Test
    @DisplayName("create a new hotel - success")
    public void testCreateHotel_Success() throws Exception {
        doNothing().when(hotelService).createHotel(any(CreateHotelRequest.class));

        var requestBody = """
                {
                  "name": "Grand Hotel",
                  "email": "contact@grandhotel.com",
                  "password": "securePass123",
                  "address": {
                    "no": "10",
                    "street": "Main St",
                    "city": "Colombo",
                    "province": "Western"
                  },
                  "contactNumbers": [
                    {"number": "+123456789"}
                  ],
                  "status": true,
                  "boostPackageLimit": "5",
                  "superDealLimit": "10"
                }
                """;

        mockMvc.perform(
                post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-Api-Version", "v1")
        ).andExpect(status().isOk());

        verify(hotelService, times(1)).createHotel(any(CreateHotelRequest.class));
    }

    @Test
    @DisplayName("create hotel - service throws exception")
    public void testCreateHotel_ServiceException() throws Exception {
        doThrow(new HotelNotCreatedException("Failed to create hotel")).when(hotelService).createHotel(any(CreateHotelRequest.class));

        var requestBody = """
                {
                  "name": "Grand Hotel",
                  "email": "contact@grandhotel.com",
                  "password": "securePass123",
                  "address": {
                    "no": "10",
                    "street": "Main St",
                    "city": "Colombo",
                    "province": "Western"
                  },
                  "status": true
                }
                """;

        mockMvc.perform(
                post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-Api-Version", "v1")
        ).andExpect(status().isInternalServerError());

        verify(hotelService, times(1)).createHotel(any(CreateHotelRequest.class));
    }

    // GET ALL HOTELS TESTS
    @Test
    @DisplayName("get all hotels - success")
    public void testGetAllHotels_Success() throws Exception {
        Hotel hotel1 = createHotel(1L, "Hotel A", "hotela@example.com", "10", "Main St", "Colombo", "Western", true);
        Hotel hotel2 = createHotel(2L, "Hotel B", "hotelb@example.com", "20", "Park Rd", "Kandy", "Central", false);
        var hotelList = List.of(hotel1, hotel2);
        when(hotelService.findAll()).thenReturn(hotelList);

        mockMvc.perform(
                        get("/hotels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelList.length()").value(2))
                .andExpect(jsonPath("$.hotelList[0].id").value(1))
                .andExpect(jsonPath("$.hotelList[0].name").value("Hotel A"))
                .andExpect(jsonPath("$.hotelList[0].email").value("hotela@example.com"))
                .andExpect(jsonPath("$.hotelList[0].address.no").value("10"))
                .andExpect(jsonPath("$.hotelList[0].address.street").value("Main St"))
                .andExpect(jsonPath("$.hotelList[0].address.city").value("Colombo"))
                .andExpect(jsonPath("$.hotelList[0].address.province").value("Western"))
                .andExpect(jsonPath("$.hotelList[0].status").value(true))
                .andExpect(jsonPath("$.hotelList[0].contactNumbers[0].number").value("+123456789"))
                .andExpect(jsonPath("$.hotelList[1].id").value(2))
                .andExpect(jsonPath("$.hotelList[1].name").value("Hotel B"))
                .andExpect(jsonPath("$.hotelList[1].email").value("hotelb@example.com"))
                .andExpect(jsonPath("$.hotelList[1].address.no").value("20"))
                .andExpect(jsonPath("$.hotelList[1].address.street").value("Park Rd"))
                .andExpect(jsonPath("$.hotelList[1].address.city").value("Kandy"))
                .andExpect(jsonPath("$.hotelList[1].address.province").value("Central"))
                .andExpect(jsonPath("$.hotelList[1].status").value(false));

        verify(hotelService, times(1)).findAll();
    }

    @Test
    @DisplayName("get all hotels when no hotels exist")
    public void testGetAllHotels_EmptyList() throws Exception {
        when(hotelService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/hotels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelList.length()").value(0));

        verify(hotelService, times(1)).findAll();
    }

    // GET HOTEL BY ID TESTS
    @Test
    @DisplayName("get hotel by id - success")
    public void testGetHotelById_Success() throws Exception {
        Hotel hotel = createHotel(1L, "Hotel A", "hotela@example.com", "10", "Main St", "Colombo", "Western", true);
        when(hotelService.findById(1L)).thenReturn(hotel);

        mockMvc.perform(
                        get("/hotels/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelListItem.id").value(1))
                .andExpect(jsonPath("$.hotelListItem.name").value("Hotel A"))
                .andExpect(jsonPath("$.hotelListItem.email").value("hotela@example.com"))
                .andExpect(jsonPath("$.hotelListItem.address.no").value("10"))
                .andExpect(jsonPath("$.hotelListItem.address.street").value("Main St"))
                .andExpect(jsonPath("$.hotelListItem.address.city").value("Colombo"))
                .andExpect(jsonPath("$.hotelListItem.address.province").value("Western"))
                .andExpect(jsonPath("$.hotelListItem.status").value(true))
                .andExpect(jsonPath("$.hotelListItem.boostPackageLimit").value("5"))
                .andExpect(jsonPath("$.hotelListItem.superDealLimit").value("10"))
                .andExpect(jsonPath("$.hotelListItem.contactNumbers[0].number").value("+123456789"));

        verify(hotelService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("get hotel by id - not found")
    public void testGetHotelById_NotFound() throws Exception {
        when(hotelService.findById(1L)).thenThrow(new HotelNotFoundException("Hotel not found with ID: 1"));

        mockMvc.perform(
                        get("/hotels/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isNotFound())
                .andExpect(content().string("Hotel not found with ID: 1"));

        verify(hotelService, times(1)).findById(1L);
    }

    // UPDATE HOTEL TESTS
    @Test
    @DisplayName("update hotel - success")
    public void testUpdateHotel_Success() throws Exception {
        doNothing().when(hotelService).updateHotel(eq(1L), any(UpdateHotelRequest.class));

        var requestBody = """
                {
                  "password": "newPassword123",
                  "hotelImageDTO": [
                    {"url": "http://example.com/new-image.jpg"}
                  ],
                  "hotelContactNumberDTO": [
                    {"number": "+987654321"}
                  ],
                  "boostPackageLimit": "10",
                  "superDealLimit": "20",
                  "status": false
                }
                """;

        mockMvc.perform(
                patch("/hotels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-Api-Version", "v1")
        ).andExpect(status().isOk());

        verify(hotelService, times(1)).updateHotel(eq(1L), any(UpdateHotelRequest.class));
    }

    @Test
    @DisplayName("update hotel - not found")
    public void testUpdateHotel_NotFound() throws Exception {
        doThrow(new HotelNotFoundException("Hotel not found with ID: 1")).when(hotelService).updateHotel(eq(1L), any(UpdateHotelRequest.class));

        var requestBody = """
                {
                  "password": "newPassword123",
                  "status": false
                }
                """;

        mockMvc.perform(
                patch("/hotels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-Api-Version", "v1")
        ).andExpect(status().isNotFound());

        verify(hotelService, times(1)).updateHotel(eq(1L), any(UpdateHotelRequest.class));
    }

    // DELETE HOTEL TESTS
    @Test
    @DisplayName("delete hotel - success")
    public void testDeleteHotel_Success() throws Exception {
        doNothing().when(hotelService).deleteHotel(1L);

        mockMvc.perform(
                delete("/hotels/1")
                        .header("X-Api-Version", "v1")
        ).andExpect(status().isOk());

        verify(hotelService, times(1)).deleteHotel(1L);
    }

    @Test
    @DisplayName("delete hotel - not found")
    public void testDeleteHotel_NotFound() throws Exception {
        doThrow(new HotelNotFoundException("Hotel not found with ID: 1")).when(hotelService).deleteHotel(1L);

        mockMvc.perform(
                delete("/hotels/1")
                        .header("X-Api-Version", "v1")
        ).andExpect(status().isNotFound());

        verify(hotelService, times(1)).deleteHotel(1L);
    }

    // FILTER BY STATUS TESTS
    @Test
    @DisplayName("filter hotels by status - success")
    public void testFilterByStatus_Success() throws Exception {
        Hotel hotel1 = createHotel(1L, "Hotel A", "hotela@example.com", "10", "Main St", "Colombo", "Western", true);
        Hotel hotel2 = createHotel(2L, "Hotel B", "hotelb@example.com", "20", "Park Rd", "Kandy", "Central", true);
        var hotelList = List.of(hotel1, hotel2);
        when(hotelService.findByStatus(true)).thenReturn(hotelList);

        mockMvc.perform(
                        get("/hotels/filterByStatus")
                                .param("status", "true")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelList.length()").value(2))
                .andExpect(jsonPath("$.hotelList[0].id").value(1))
                .andExpect(jsonPath("$.hotelList[0].name").value("Hotel A"))
                .andExpect(jsonPath("$.hotelList[0].email").value("hotela@example.com"))
                .andExpect(jsonPath("$.hotelList[0].status").value(true))
                .andExpect(jsonPath("$.hotelList[1].id").value(2))
                .andExpect(jsonPath("$.hotelList[1].name").value("Hotel B"))
                .andExpect(jsonPath("$.hotelList[1].email").value("hotelb@example.com"))
                .andExpect(jsonPath("$.hotelList[1].status").value(true));

        verify(hotelService, times(1)).findByStatus(true);
    }

    @Test
    @DisplayName("filter hotels by status when no matches")
    public void testFilterByStatus_NoMatches() throws Exception {
        when(hotelService.findByStatus(false)).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/hotels/filterByStatus")
                                .param("status", "false")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelList.length()").value(0));

        verify(hotelService, times(1)).findByStatus(false);
    }

    @Test
    @DisplayName("filter hotels by status - not found exception")
    public void testFilterByStatus_NotFound() throws Exception {
        when(hotelService.findByStatus(true)).thenThrow(new HotelNotFoundException("No hotels found with status: true"));

        mockMvc.perform(
                        get("/hotels/filterByStatus")
                                .param("status", "true")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v1")
                ).andExpect(status().isNotFound());

        verify(hotelService, times(1)).findByStatus(true);
    }

    // GET PENDING HOTELS TESTS
    @Test
    @DisplayName("get pending hotels - success")
    public void testGetPendingHotels_Success() throws Exception {
        Hotel hotel1 = createHotel(1L, "Hotel A", "hotela@example.com", "10", "Main St", "Colombo", "Western", true);
        Hotel hotel2 = createHotel(2L, "Hotel B", "hotelb@example.com", "20", "Park Rd", "Kandy", "Central", true);
        var hotelList = List.of(hotel1, hotel2);
        when(hotelService.getPendingHotels()).thenReturn(hotelList);

        mockMvc.perform(
                        get("/pending")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Hotel A"))
                .andExpect(jsonPath("$[0].email").value("hotela@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Hotel B"))
                .andExpect(jsonPath("$[1].email").value("hotelb@example.com"));

        verify(hotelService, times(1)).getPendingHotels();
    }

    @Test
    @DisplayName("get pending hotels - empty list")
    public void testGetPendingHotels_EmptyList() throws Exception {
        when(hotelService.getPendingHotels()).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/pending")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(hotelService, times(1)).getPendingHotels();
    }

    // APPROVE HOTEL TESTS
    @Test
    @DisplayName("approve hotel - success")
    public void testApproveHotel_Success() throws Exception {
        doNothing().when(hotelService).approveHotel(1L);

        mockMvc.perform(
                        put("/1/approve")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().string("Hotel Approved Successfully"));

        verify(hotelService, times(1)).approveHotel(1L);
    }

    @Test
    @DisplayName("approve hotel - not found")
    public void testApproveHotel_NotFound() throws Exception {
        doThrow(new HotelNotFoundException("Hotel not found with ID: 1")).when(hotelService).approveHotel(1L);

        mockMvc.perform(
                        put("/1/approve")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().string("Hotel not found with ID: 1"));

        verify(hotelService, times(1)).approveHotel(1L);
    }

    @Test
    @DisplayName("approve hotel - approval exception")
    public void testApproveHotel_ApprovalException() throws Exception {
        doThrow(new HotelApprovalException("Hotel is already approved")).when(hotelService).approveHotel(1L);

        mockMvc.perform(
                        put("/1/approve")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(content().string("Hotel is already approved"));

        verify(hotelService, times(1)).approveHotel(1L);
    }

    // REJECT HOTEL TESTS
    @Test
    @DisplayName("reject hotel - success")
    public void testRejectHotel_Success() throws Exception {
        doNothing().when(hotelService).rejectHotel(1L);

        mockMvc.perform(
                        put("/1/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().string("Hotel Rejected Successfully"));

        verify(hotelService, times(1)).rejectHotel(1L);
    }

    @Test
    @DisplayName("reject hotel - not found")
    public void testRejectHotel_NotFound() throws Exception {
        doThrow(new HotelNotFoundException("Hotel not found with ID: 1")).when(hotelService).rejectHotel(1L);

        mockMvc.perform(
                        put("/1/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().string("Hotel not found with ID: 1"));

        verify(hotelService, times(1)).rejectHotel(1L);
    }

    @Test
    @DisplayName("reject hotel - approval exception")
    public void testRejectHotel_ApprovalException() throws Exception {
        doThrow(new HotelApprovalException("Hotel is already rejected")).when(hotelService).rejectHotel(1L);

        mockMvc.perform(
                        put("/1/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(content().string("Hotel is already rejected"));

        verify(hotelService, times(1)).rejectHotel(1L);
    }

    // API VERSION TESTS
    @Test
    @DisplayName("wrong API version - not found")
    public void testWrongApiVersion_NotFound() throws Exception {
        mockMvc.perform(
                        get("/hotels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Api-Version", "v2")
                ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("missing API version - not found")
    public void testMissingApiVersion_NotFound() throws Exception {
        mockMvc.perform(
                        get("/hotels")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

    // HELPER METHODS
    private Hotel createHotel(Long id, String name, String email, String no, String street, String city, String province, boolean status) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setName(name);
        hotel.setEmail(email);
        hotel.setPassword("securePass123");
        Address address = new Address();
        address.setNo(no);
        address.setStreet(street);
        address.setCity(city);
        address.setProvince(province);
        hotel.setAddress(address);
        hotel.setStatus(status);
        hotel.setBoostPackageLimit("5");
        hotel.setSuperDealLimit("10");

        HotelContactNumber contactNumber = new HotelContactNumber();
        contactNumber.setNumber("+123456789");
        contactNumber.setHotel(hotel);
        hotel.setContactNumbers(List.of(contactNumber));

        return hotel;
    }
} 