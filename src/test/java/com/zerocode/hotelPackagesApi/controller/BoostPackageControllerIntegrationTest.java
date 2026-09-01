package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.BoostPackageRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class BoostPackageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoostPackageRepository boostPackageRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Hotel testHotel;
    private HotelPackage testHotelPackage;

    @BeforeEach
    void setup() {
        boostPackageRepository.deleteAll();
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED); // Set required field
        testHotel = hotelRepository.saveAndFlush(testHotel);

        testHotelPackage = createTestHotelPackage();
        testHotelPackage.setHotel(testHotel);
        testHotelPackage.setBoostPackage(null);
        testHotelPackage = packageRepository.saveAndFlush(testHotelPackage);
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    @DisplayName("Create a new boost package - Success")
    void testCreateBoostPackage() throws Exception {
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));
        String requestBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/packages/{package-id}/boost-packages", testHotelPackage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Api-Version", "v1")
                        .content(requestBody))
                .andExpect(status().isOk());

        assertThat(boostPackageRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    @DisplayName("Create a new boost package - Package Not Found")
    void testCreateBoostPackagePackageNotFound() throws Exception {
        CreateBoostPackageRequestDTO requestDTO = new CreateBoostPackageRequestDTO(LocalDate.of(2025, 4, 10));
        String requestBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/packages/{package-id}/boost-packages", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Api-Version", "v1")
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    @DisplayName("Get boost package by ID - Success")
    void testGetBoostPackageById() throws Exception {
        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage.setHotelPackage(testHotelPackage);
        boostPackage.setHotel(testHotel);
        boostPackage = boostPackageRepository.saveAndFlush(boostPackage);

        testHotelPackage.setBoostPackage(boostPackage);
        packageRepository.saveAndFlush(testHotelPackage);

        mockMvc.perform(get("/boost-packages/{boost-package-id}", boostPackage.getId())
                        .header("X-Api-Version", "v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(boostPackage.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testHotelPackage.getName())))
                .andExpect(jsonPath("$.boostedDate", is("2025-04-10")));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    @DisplayName("Get boost package by ID - Not Found")
    void testGetBoostPackageByIdNotFound() throws Exception {
        mockMvc.perform(get("/boost-packages/{boost-package-id}", 999L)
                        .header("X-Api-Version", "v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Get all boost packages - Success")
    void testGetBoostPackages() throws Exception {
        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage.setHotelPackage(testHotelPackage);
        boostPackage.setHotel(testHotel);
        boostPackage = boostPackageRepository.saveAndFlush(boostPackage);

        testHotelPackage.setBoostPackage(boostPackage);
        packageRepository.saveAndFlush(testHotelPackage);

        mockMvc.perform(get("/boost-packages")
                        .header("X-Api-Version", "v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boostPackageLists[0].name", is(testHotelPackage.getName())))
                .andExpect(jsonPath("$.boostPackageLists[0].boostedDate", is("2025-04-10")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Get all boost packages - No Boost Packages Found")
    void testGetBoostPackagesNotFound() throws Exception {
        mockMvc.perform(get("/boost-packages")
                        .header("X-Api-Version", "v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    @DisplayName("Delete boost package by ID - Success")
    void testDeleteBoostPackage() throws Exception {
        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(LocalDate.of(2025, 4, 10));
        boostPackage.setHotelPackage(testHotelPackage);
        boostPackage.setHotel(testHotel);
        boostPackage = boostPackageRepository.saveAndFlush(boostPackage);

        testHotelPackage.setBoostPackage(boostPackage);
        packageRepository.saveAndFlush(testHotelPackage);

        mockMvc.perform(delete("/boost-packages/{boost-package-id}", boostPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        assertThat(boostPackageRepository.findById(boostPackage.getId()).isEmpty(), is(true));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    @DisplayName("Delete boost package by ID - Not Found")
    void testDeleteBoostPackageNotFound() throws Exception {
        mockMvc.perform(delete("/boost-packages/{boost-package-id}", 999L)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    private HotelPackage createTestHotelPackage() {
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);

        HotelPackage hotelPackage = new HotelPackage();
        hotelPackage.setName("Test Package");
        hotelPackage.setPrice(100.0);
        hotelPackage.setDescription("A test package");
        hotelPackage.setStartDate(LocalDate.now());
        hotelPackage.setEndDate(LocalDate.now().plusDays(7));
        hotelPackage.setTermsAndCondition("No refunds");
        hotelPackage.setVisitorCount(0);
        hotelPackage.setStatus(true);
        hotelPackage.setGuestCount(guestCount);
        return hotelPackage;
    }
}
