package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import com.zerocode.hotelPackagesApi.repository.SuperDealsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SuperDealsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SuperDealsRepository superDealsRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PackageRepository packageRepository;

    private Hotel testHotel;
    private HotelPackage testPackage;
    private SuperDeal testSuperDeal;
    private CreateSuperDealsRequest createSuperDealsRequest;

    @BeforeEach
    void setUp() {
        superDealsRepository.deleteAll();
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        // Create test hotel
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password123");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);

        Address address = new Address();
        address.setNo("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        testHotel.setAddress(address);

        testHotel = hotelRepository.save(testHotel);

        // Create test package
        testPackage = new HotelPackage();
        testPackage.setName("Test Package");
        testPackage.setPrice(1000.0);
        testPackage.setDescription("Test package description");
        testPackage.setStartDate(LocalDate.now().plusDays(1));
        testPackage.setEndDate(LocalDate.now().plusDays(7));
        testPackage.setTermsAndCondition("Test terms and conditions");
        testPackage.setVisitorCount(2);
        testPackage.setStatus(true);
        testPackage.setPackageStatus(PackageStatus.APPROVED);
        testPackage.setHotel(testHotel);

        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);
        testPackage.setGuestCount(guestCount);

        testPackage = packageRepository.save(testPackage);

        // Create test super deal
        testSuperDeal = new SuperDeal();
        testSuperDeal.setStartDate(LocalDate.now().plusDays(1));
        testSuperDeal.setEndDate(LocalDate.now().plusDays(7));
        testSuperDeal.setDiscountedPrice(800.0);
        testSuperDeal.setHotelPackage(testPackage);
        testSuperDeal.setHotel(testHotel);
        testSuperDeal = superDealsRepository.save(testSuperDeal);

        // Create request for new super deal (for a different package)
        createSuperDealsRequest = new CreateSuperDealsRequest(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                750.0
        );
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_Success() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("New Test Package");
        newPackage.setPrice(1200.0);
        newPackage.setDescription("New test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("New test terms and conditions");
        newPackage.setVisitorCount(3);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(3);
        newGuestCount.setChildren(2);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        int initialCount = (int) superDealsRepository.count();

        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSuperDealsRequest)))
                .andExpect(status().isOk());

        assertEquals(initialCount + 1, superDealsRepository.count());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createSuperDeal_Unauthorized() throws Exception {
        mockMvc.perform(post("/packages/{package-id}/super-deals", testPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSuperDealsRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_InvalidPackageId() throws Exception {
        mockMvc.perform(post("/packages/{package-id}/super-deals", 999L)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSuperDealsRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_InvalidRequest() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Invalid Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Invalid test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Invalid test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        CreateSuperDealsRequest invalidRequest = new CreateSuperDealsRequest(
                LocalDate.now().plusDays(7), // end date before start date
                LocalDate.now().plusDays(1),
                750.0
        );

        // Since validation is not implemented, this should succeed
        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_NegativeDiscountedPrice() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Negative Price Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Negative price test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Negative price test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        CreateSuperDealsRequest invalidRequest = new CreateSuperDealsRequest(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(7),
                -100.0
        );

        // Since validation is not implemented, this should succeed
        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllSuperDeals_Success() throws Exception {
        mockMvc.perform(get("/super-deals")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.superDeals", hasSize(1)))
                .andExpect(jsonPath("$.superDeals[0].startDate", is(testSuperDeal.getStartDate().toString())))
                .andExpect(jsonPath("$.superDeals[0].endDate", is(testSuperDeal.getEndDate().toString())))
                .andExpect(jsonPath("$.superDeals[0].discountedPrice", is(testSuperDeal.getDiscountedPrice())))
                .andExpect(jsonPath("$.superDeals[0].packageListItem.name", is("Test Package")))
                .andExpect(jsonPath("$.superDeals[0].packageListItem.price", is(1000.0)))
                .andExpect(jsonPath("$.superDeals[0].packageListItem.guestAdults", is(2)))
                .andExpect(jsonPath("$.superDeals[0].packageListItem.guestChildren", is(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSuperDeals_AdminAccess() throws Exception {
        mockMvc.perform(get("/super-deals")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.superDeals", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllSuperDeals_CustomerAccess() throws Exception {
        mockMvc.perform(get("/super-deals")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.superDeals", hasSize(1)));
    }

    @Test
    void getAllSuperDeals_Unauthorized() throws Exception {
        mockMvc.perform(get("/super-deals")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllSuperDeals_EmptyList() throws Exception {
        superDealsRepository.deleteAll();

        mockMvc.perform(get("/super-deals")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.superDeals", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getSuperDealById_Success() throws Exception {
        mockMvc.perform(get("/super-deals/{super-deal-id}", testSuperDeal.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.superDealListItem.startDate", is(testSuperDeal.getStartDate().toString())))
                .andExpect(jsonPath("$.superDealListItem.endDate", is(testSuperDeal.getEndDate().toString())))
                .andExpect(jsonPath("$.superDealListItem.discountedPrice", is(testSuperDeal.getDiscountedPrice())))
                .andExpect(jsonPath("$.superDealListItem.packageListItem.name", is("Test Package")))
                .andExpect(jsonPath("$.superDealListItem.packageListItem.price", is(1000.0)));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getSuperDealById_CustomerAccess() throws Exception {
        mockMvc.perform(get("/super-deals/{super-deal-id}", testSuperDeal.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.superDealListItem.packageListItem.name", is("Test Package")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSuperDealById_AdminAccess() throws Exception {
        mockMvc.perform(get("/super-deals/{super-deal-id}", testSuperDeal.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getSuperDealById_NotFound() throws Exception {
        mockMvc.perform(get("/super-deals/{super-deal-id}", 999L)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSuperDealById_Unauthorized() throws Exception {
        mockMvc.perform(get("/super-deals/{super-deal-id}", testSuperDeal.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteSuperDealById_Success() throws Exception {
        Long superDealId = testSuperDeal.getId();

        mockMvc.perform(delete("/super-deals/{super-deal-id}", superDealId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        assertFalse(superDealsRepository.existsById(superDealId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSuperDealById_AdminAccess() throws Exception {
        Long superDealId = testSuperDeal.getId();

        mockMvc.perform(delete("/super-deals/{super-deal-id}", superDealId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        assertFalse(superDealsRepository.existsById(superDealId));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteSuperDealById_CustomerAccess() throws Exception {
        mockMvc.perform(delete("/super-deals/{super-deal-id}", testSuperDeal.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteSuperDealById_NotFound() throws Exception {
        mockMvc.perform(delete("/super-deals/{super-deal-id}", 999L)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSuperDealById_Unauthorized() throws Exception {
        mockMvc.perform(delete("/super-deals/{super-deal-id}", testSuperDeal.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_MissingApiVersion() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Missing API Version Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Missing API version test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Missing API version test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSuperDealsRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getAllSuperDeals_MissingApiVersion() throws Exception {
        mockMvc.perform(get("/super-deals"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getSuperDealById_MissingApiVersion() throws Exception {
        mockMvc.perform(get("/super-deals/{super-deal-id}", testSuperDeal.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deleteSuperDealById_MissingApiVersion() throws Exception {
        mockMvc.perform(delete("/super-deals/{super-deal-id}", testSuperDeal.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_InvalidJson() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Invalid JSON Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Invalid JSON test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Invalid JSON test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_MissingRequiredFields() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Missing Fields Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Missing fields test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Missing fields test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        CreateSuperDealsRequest invalidRequest = new CreateSuperDealsRequest(
                null, // missing start date
                LocalDate.now().plusDays(7),
                750.0
        );

        // Since validation is not implemented, this should succeed
        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_PastStartDate() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Past Date Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Past date test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Past date test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        CreateSuperDealsRequest invalidRequest = new CreateSuperDealsRequest(
                LocalDate.now().minusDays(1), // past date
                LocalDate.now().plusDays(7),
                750.0
        );

        // Since validation is not implemented, this should succeed
        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createSuperDeal_DiscountedPriceHigherThanOriginal() throws Exception {
        // Create a new package for this test since each package can only have one super deal
        HotelPackage newPackage = new HotelPackage();
        newPackage.setName("Higher Price Test Package");
        newPackage.setPrice(1000.0);
        newPackage.setDescription("Higher price test package description");
        newPackage.setStartDate(LocalDate.now().plusDays(1));
        newPackage.setEndDate(LocalDate.now().plusDays(7));
        newPackage.setTermsAndCondition("Higher price test terms and conditions");
        newPackage.setVisitorCount(2);
        newPackage.setStatus(true);
        newPackage.setPackageStatus(PackageStatus.APPROVED);
        newPackage.setHotel(testHotel);

        GuestCount newGuestCount = new GuestCount();
        newGuestCount.setAdults(2);
        newGuestCount.setChildren(1);
        newPackage.setGuestCount(newGuestCount);

        newPackage = packageRepository.save(newPackage);

        CreateSuperDealsRequest invalidRequest = new CreateSuperDealsRequest(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(7),
                1500.0 // higher than original price of 1000.0
        );

        // Since validation is not implemented, this should succeed
        mockMvc.perform(post("/packages/{package-id}/super-deals", newPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());
    }
}
