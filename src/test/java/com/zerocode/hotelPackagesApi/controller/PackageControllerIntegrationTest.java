package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PackageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private HotelPackage testPackage;
    private CreatePackageRequestDTO createPackageRequestDTO;

    @BeforeEach
    void setUp() {
        // Clear repositories
        packageRepository.deleteAll();
        hotelRepository.deleteAll();

        // Create test hotel with Address object
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");

        // Create Address object with correct fields
        Address address = new Address();
        address.setNo("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        testHotel.setAddress(address);

        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.save(testHotel);

        // Create test package
        testPackage = new HotelPackage();
        testPackage.setName("Test Package");
        testPackage.setDescription("Test Description");
        testPackage.setPrice(999.99);
        testPackage.setStartDate(LocalDate.now().plusDays(1));
        testPackage.setEndDate(LocalDate.now().plusDays(7));
        testPackage.setTermsAndCondition("Test Terms");

        // Create GuestCount using default constructor
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(1);
        testPackage.setGuestCount(guestCount);

        testPackage.setStatus(true);
        testPackage.setHotel(testHotel);
        testPackage.setPackageStatus(PackageStatus.PENDING);

        // Create PackageImage - adjust based on your actual model
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg"); // or setImage() or whatever the field is called
        image.setHotelPackage(testPackage);
        testPackage.setPackageImages(Arrays.asList(image));

        testPackage = packageRepository.save(testPackage);

        // Create DTO for create/update operations
        createPackageRequestDTO = new CreatePackageRequestDTO();
        createPackageRequestDTO.setName("New Package");
        createPackageRequestDTO.setDescription("New Description");
        createPackageRequestDTO.setStartDate(LocalDate.now().plusDays(2));
        createPackageRequestDTO.setEndDate(LocalDate.now().plusDays(10));

        // Create GuestCount for DTO
        GuestCount dtoGuestCount = new GuestCount();
        dtoGuestCount.setAdults(2);
        dtoGuestCount.setChildren(0);
        createPackageRequestDTO.setGuestCount(dtoGuestCount);

        createPackageRequestDTO.setVisitors(4);
        createPackageRequestDTO.setPrice(1299.99);
        createPackageRequestDTO.setTermsAndCondition("New Terms");

        PackageImage newImage = new PackageImage();
        newImage.setUrl("http://example.com/new-image.jpg"); // Adjust field name as needed
        createPackageRequestDTO.setImages(Arrays.asList(newImage));
        createPackageRequestDTO.setStatus(true);
    }

    // CREATE PACKAGE TESTS
    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_Success() throws Exception {
        int initialCount = (int) packageRepository.count();

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isOk());

        assertEquals(initialCount + 1, packageRepository.count());

        HotelPackage createdPackage = packageRepository.findAll().stream()
                .filter(p -> p.getName().equals("New Package"))
                .findFirst()
                .orElse(null);

        assertNotNull(createdPackage);
        assertEquals("New Package", createdPackage.getName());
        assertEquals(testHotel.getId(), createdPackage.getHotel().getId());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_HotelNotFound() throws Exception {
        Long nonExistentHotelId = 9999L;

        mockMvc.perform(post("/hotels/{hotel-id}/packages", nonExistentHotelId)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createPackage_Forbidden() throws Exception {
        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPackage_AdminForbidden() throws Exception {
        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_InvalidDateRange() throws Exception {
        createPackageRequestDTO.setStartDate(LocalDate.now().plusDays(10));
        createPackageRequestDTO.setEndDate(LocalDate.now().plusDays(5)); // End before start

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_PastStartDate() throws Exception {
        createPackageRequestDTO.setStartDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_InvalidPrice() throws Exception {
        createPackageRequestDTO.setPrice(-100.0);

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_InvalidVisitors() throws Exception {
        createPackageRequestDTO.setVisitors(0);

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_NoImages() throws Exception {
        createPackageRequestDTO.setImages(Arrays.asList());

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_MissingName() throws Exception {
        CreatePackageRequestDTO invalidDto = new CreatePackageRequestDTO();
        invalidDto.setDescription("Test Description");
        invalidDto.setStartDate(LocalDate.now().plusDays(1));
        invalidDto.setEndDate(LocalDate.now().plusDays(7));
        
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(0);
        invalidDto.setGuestCount(guestCount);
        
        invalidDto.setVisitors(4);
        invalidDto.setPrice(999.99);
        invalidDto.setTermsAndCondition("Test Terms");
        
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        invalidDto.setImages(Arrays.asList(image));
        invalidDto.setStatus(true);

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_MissingDescription() throws Exception {
        CreatePackageRequestDTO invalidDto = new CreatePackageRequestDTO();
        invalidDto.setName("Test Package");
        invalidDto.setStartDate(LocalDate.now().plusDays(1));
        invalidDto.setEndDate(LocalDate.now().plusDays(7));
        
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(0);
        invalidDto.setGuestCount(guestCount);
        
        invalidDto.setVisitors(4);
        invalidDto.setPrice(999.99);
        invalidDto.setTermsAndCondition("Test Terms");
        
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        invalidDto.setImages(Arrays.asList(image));
        invalidDto.setStatus(true);

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_MissingGuestCount() throws Exception {
        CreatePackageRequestDTO invalidDto = new CreatePackageRequestDTO();
        invalidDto.setName("Test Package");
        invalidDto.setDescription("Test Description");
        invalidDto.setStartDate(LocalDate.now().plusDays(1));
        invalidDto.setEndDate(LocalDate.now().plusDays(7));
        invalidDto.setVisitors(4);
        invalidDto.setPrice(999.99);
        invalidDto.setTermsAndCondition("Test Terms");
        
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        invalidDto.setImages(Arrays.asList(image));
        invalidDto.setStatus(true);

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_MissingTermsAndCondition() throws Exception {
        CreatePackageRequestDTO invalidDto = new CreatePackageRequestDTO();
        invalidDto.setName("Test Package");
        invalidDto.setDescription("Test Description");
        invalidDto.setStartDate(LocalDate.now().plusDays(1));
        invalidDto.setEndDate(LocalDate.now().plusDays(7));
        
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(0);
        invalidDto.setGuestCount(guestCount);
        
        invalidDto.setVisitors(4);
        invalidDto.setPrice(999.99);
        
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        invalidDto.setImages(Arrays.asList(image));
        invalidDto.setStatus(true);

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_MissingStatus() throws Exception {
        CreatePackageRequestDTO invalidDto = new CreatePackageRequestDTO();
        invalidDto.setName("Test Package");
        invalidDto.setDescription("Test Description");
        invalidDto.setStartDate(LocalDate.now().plusDays(1));
        invalidDto.setEndDate(LocalDate.now().plusDays(7));
        
        GuestCount guestCount = new GuestCount();
        guestCount.setAdults(2);
        guestCount.setChildren(0);
        invalidDto.setGuestCount(guestCount);
        
        invalidDto.setVisitors(4);
        invalidDto.setPrice(999.99);
        invalidDto.setTermsAndCondition("Test Terms");
        
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        invalidDto.setImages(Arrays.asList(image));

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_InvalidVisitorsMax() throws Exception {
        createPackageRequestDTO.setVisitors(101); // Exceeds max value

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_NameTooLong() throws Exception {
        createPackageRequestDTO.setName("A".repeat(101)); // Exceeds max length

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_ValidationError_DescriptionTooLong() throws Exception {
        createPackageRequestDTO.setDescription("A".repeat(501)); // Exceeds max length

        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    // GET ALL PACKAGES TESTS
    @Test
    @WithMockUser(roles = {"CUSTOMER", "HOTEL", "ADMIN"})
    void getAllPackages_Success() throws Exception {
        mockMvc.perform(get("/packages")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(1)))
                .andExpect(jsonPath("$.packages[0].name", is("Test Package")));
    }

    @Test
    void getAllPackages_Unauthorized() throws Exception {
        mockMvc.perform(get("/packages")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    // GET PACKAGE BY ID TESTS
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPackageById_Success() throws Exception {
        mockMvc.perform(get("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Package")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(999.99)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackageById_AsHotel_Success() throws Exception {
        mockMvc.perform(get("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Package")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackageById_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Package")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPackageById_NotFound() throws Exception {
        Long nonExistentPackageId = 9999L;

        mockMvc.perform(get("/packages/{package-id}", nonExistentPackageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPackageById_Unauthorized() throws Exception {
        mockMvc.perform(get("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    // DELETE PACKAGE TESTS
    @Test
    @WithMockUser(roles = "HOTEL")
    void deletePackageById_Success() throws Exception {
        Long packageId = testPackage.getId();

        mockMvc.perform(delete("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        assertFalse(packageRepository.existsById(packageId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePackageById_AsAdmin_Success() throws Exception {
        Long packageId = testPackage.getId();

        mockMvc.perform(delete("/packages/{package-id}", packageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());

        assertFalse(packageRepository.existsById(packageId));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deletePackageById_Forbidden() throws Exception {
        mockMvc.perform(delete("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deletePackageById_NotFound() throws Exception {
        Long nonExistentPackageId = 9999L;

        mockMvc.perform(delete("/packages/{package-id}", nonExistentPackageId)
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePackageById_Unauthorized() throws Exception {
        mockMvc.perform(delete("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    // UPDATE PACKAGE TESTS
    @Test
    @WithMockUser(roles = "HOTEL")
    void updatePackageById_Success() throws Exception {
        mockMvc.perform(put("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isOk());

        HotelPackage updatedPackage = packageRepository.findById(testPackage.getId()).orElse(null);
        assertNotNull(updatedPackage);
        assertEquals("New Package", updatedPackage.getName());
        assertEquals("New Description", updatedPackage.getDescription());
        assertEquals(1299.99, updatedPackage.getPrice());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updatePackageById_NotFound() throws Exception {
        Long nonExistentPackageId = 9999L;

        mockMvc.perform(put("/packages/{package-id}", nonExistentPackageId)
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updatePackageById_Forbidden() throws Exception {
        mockMvc.perform(put("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePackageById_AdminForbidden() throws Exception {
        mockMvc.perform(put("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updatePackageById_ValidationError() throws Exception {
        CreatePackageRequestDTO invalidDto = new CreatePackageRequestDTO();
        // Missing required fields but provide images to avoid NPE
        PackageImage image = new PackageImage();
        image.setUrl("http://example.com/image.jpg");
        invalidDto.setImages(Arrays.asList(image));

        mockMvc.perform(put("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePackageById_Unauthorized() throws Exception {
        mockMvc.perform(put("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isForbidden());
    }

    // GET PACKAGES BY STATUS TESTS
    @Test
    @WithMockUser(roles = {"HOTEL", "ADMIN"})
    void getPackagesByStatus_Active() throws Exception {
        // Create an inactive package
        HotelPackage inactivePackage = new HotelPackage();
        inactivePackage.setName("Inactive Package");
        inactivePackage.setDescription("Inactive Description");
        inactivePackage.setPrice(799.99);
        inactivePackage.setStartDate(LocalDate.now().plusDays(1));
        inactivePackage.setEndDate(LocalDate.now().plusDays(5));
        inactivePackage.setTermsAndCondition("Terms");

        GuestCount inactiveGuestCount = new GuestCount();
        inactiveGuestCount.setAdults(2);
        inactiveGuestCount.setChildren(0);
        inactivePackage.setGuestCount(inactiveGuestCount);

        inactivePackage.setStatus(false);
        inactivePackage.setHotel(testHotel);
        inactivePackage.setPackageStatus(PackageStatus.PENDING);
        packageRepository.save(inactivePackage);

        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(1)))
                .andExpect(jsonPath("$.packages[0].status", is(true)));
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackagesByStatus_Inactive() throws Exception {
        // Create an inactive package
        HotelPackage inactivePackage = new HotelPackage();
        inactivePackage.setName("Inactive Package");
        inactivePackage.setDescription("Inactive Description");
        inactivePackage.setPrice(799.99);
        inactivePackage.setStartDate(LocalDate.now().plusDays(1));
        inactivePackage.setEndDate(LocalDate.now().plusDays(5));
        inactivePackage.setTermsAndCondition("Terms");

        GuestCount inactiveGuestCount = new GuestCount();
        inactiveGuestCount.setAdults(2);
        inactiveGuestCount.setChildren(0);
        inactivePackage.setGuestCount(inactiveGuestCount);

        inactivePackage.setStatus(false);
        inactivePackage.setHotel(testHotel);
        inactivePackage.setPackageStatus(PackageStatus.PENDING);
        packageRepository.save(inactivePackage);

        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(1)))
                .andExpect(jsonPath("$.packages[0].status", is(false)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackagesByStatus_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPackagesByStatus_Forbidden() throws Exception {
        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPackagesByStatus_Unauthorized() throws Exception {
        mockMvc.perform(get("/packages/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isForbidden());
    }

    // GET PACKAGES BY HOTEL ID TESTS
    @Test
    @WithMockUser(roles = {"HOTEL", "CUSTOMER"})
    void getPackagesByHotelId_Success() throws Exception {
        // Create another hotel with a package
        Hotel anotherHotel = new Hotel();
        anotherHotel.setName("Another Hotel");
        anotherHotel.setEmail("another@hotel.com");

        Address anotherAddress = new Address();
        anotherAddress.setNo("456");
        anotherAddress.setStreet("Another Street");
        anotherAddress.setCity("Another City");
        anotherAddress.setProvince("Another Province");
        anotherHotel.setAddress(anotherAddress);

        anotherHotel.setStatus(true);
        anotherHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        anotherHotel = hotelRepository.save(anotherHotel);

        HotelPackage anotherPackage = new HotelPackage();
        anotherPackage.setName("Another Package");
        anotherPackage.setDescription("Another Description");
        anotherPackage.setPrice(599.99);
        anotherPackage.setStartDate(LocalDate.now().plusDays(1));
        anotherPackage.setEndDate(LocalDate.now().plusDays(3));
        anotherPackage.setTermsAndCondition("Terms");

        GuestCount anotherGuestCount = new GuestCount();
        anotherGuestCount.setAdults(2);
        anotherGuestCount.setChildren(0);
        anotherPackage.setGuestCount(anotherGuestCount);

        anotherPackage.setStatus(true);
        anotherPackage.setHotel(anotherHotel);
        anotherPackage.setPackageStatus(PackageStatus.PENDING);
        packageRepository.save(anotherPackage);

        mockMvc.perform(get("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(1)))
                .andExpect(jsonPath("$.packages[0].name", is("Test Package")));
    }

    @Test
    void getPackagesByHotelId_Unauthorized() throws Exception {
        mockMvc.perform(get("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void getPackagesByHotelId_EmptyList() throws Exception {
        // Create a hotel with no packages
        Hotel emptyHotel = new Hotel();
        emptyHotel.setName("Empty Hotel");
        emptyHotel.setEmail("empty@hotel.com");

        Address emptyAddress = new Address();
        emptyAddress.setNo("789");
        emptyAddress.setStreet("Empty Street");
        emptyAddress.setCity("Empty City");
        emptyAddress.setProvince("Empty Province");
        emptyHotel.setAddress(emptyAddress);

        emptyHotel.setStatus(true);
        emptyHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        emptyHotel = hotelRepository.save(emptyHotel);

        mockMvc.perform(get("/hotels/{hotel-id}/packages", emptyHotel.getId())
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(0)));
    }

    // GET PACKAGES COUNT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackagesCount_Success() throws Exception {
        mockMvc.perform(get("/packages-count")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(roles = {"HOTEL", "CUSTOMER"})
    void getPackagesCount_Forbidden() throws Exception {
        mockMvc.perform(get("/packages-count")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPackagesCount_Unauthorized() throws Exception {
        mockMvc.perform(get("/packages-count")
                        .header("X-Api-Version", "v1"))
                .andExpect(status().isForbidden());
    }

    // GET PACKAGES COUNT BY STATUS TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackagesCountByStatus_Success() throws Exception {
        // Create multiple packages with different statuses
        for (int i = 0; i < 3; i++) {
            HotelPackage pkg = new HotelPackage();
            pkg.setName("Package " + i);
            pkg.setDescription("Description " + i);
            pkg.setPrice(500.0 + i * 100);
            pkg.setStartDate(LocalDate.now().plusDays(1));
            pkg.setEndDate(LocalDate.now().plusDays(5));
            pkg.setTermsAndCondition("Terms");

            GuestCount gc = new GuestCount();
            gc.setAdults(2);
            gc.setChildren(0);
            pkg.setGuestCount(gc);

            pkg.setStatus(i % 2 == 0); // Alternating status
            pkg.setHotel(testHotel);
            pkg.setPackageStatus(PackageStatus.PENDING);
            packageRepository.save(pkg);
        }

        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("3")); // Original test package + 2 new ones with status=true
    }

    @Test
    @WithMockUser(roles = {"HOTEL", "CUSTOMER"})
    void getPackagesCountByStatus_Forbidden() throws Exception {
        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPackagesCountByStatus_Unauthorized() throws Exception {
        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackagesCountByStatus_Inactive() throws Exception {
        // Create an inactive package
        HotelPackage inactivePackage = new HotelPackage();
        inactivePackage.setName("Inactive Package");
        inactivePackage.setDescription("Inactive Description");
        inactivePackage.setPrice(799.99);
        inactivePackage.setStartDate(LocalDate.now().plusDays(1));
        inactivePackage.setEndDate(LocalDate.now().plusDays(5));
        inactivePackage.setTermsAndCondition("Terms");

        GuestCount inactiveGuestCount = new GuestCount();
        inactiveGuestCount.setAdults(2);
        inactiveGuestCount.setChildren(0);
        inactivePackage.setGuestCount(inactiveGuestCount);

        inactivePackage.setStatus(false);
        inactivePackage.setHotel(testHotel);
        inactivePackage.setPackageStatus(PackageStatus.PENDING);
        packageRepository.save(inactivePackage);

        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v1")
                        .param("status", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void wrongApiVersionHeader_NotFound() throws Exception {
        mockMvc.perform(get("/packages")
                        .header("X-Api-Version", "v2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void createPackage_WrongApiVersion_NotFound() throws Exception {
        mockMvc.perform(post("/hotels/{hotel-id}/packages", testHotel.getId())
                        .header("X-Api-Version", "v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void updatePackage_WrongApiVersion_NotFound() throws Exception {
        mockMvc.perform(put("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPackageRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOTEL")
    void deletePackage_WrongApiVersion_NotFound() throws Exception {
        mockMvc.perform(delete("/packages/{package-id}", testPackage.getId())
                        .header("X-Api-Version", "v2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackagesCount_WrongApiVersion_NotFound() throws Exception {
        mockMvc.perform(get("/packages-count")
                        .header("X-Api-Version", "v2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPackagesCountByStatus_WrongApiVersion_NotFound() throws Exception {
        mockMvc.perform(get("/packages-count/findByStatus")
                        .header("X-Api-Version", "v2")
                        .param("status", "true"))
                .andExpect(status().isNotFound());
    }
}
