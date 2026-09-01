package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import com.zerocode.hotelPackagesApi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class HotelImageRepositoryTest {

    @Autowired
    private HotelImageRepository hotelImageRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private HotelImage image1;
    private HotelImage image2;

    @BeforeEach
    void setup() {
        hotelImageRepository.deleteAll();
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.save(testHotel);

        image1 = new HotelImage();
        image1.setUrl("image1.jpg");
        image1.setHotel(testHotel);

        image2 = new HotelImage();
        image2.setUrl("image2.jpg");
        image2.setHotel(testHotel);
    }

    @Test
    void testSaveAndFindById() {
        HotelImage savedImage = hotelImageRepository.save(image1);
        assertNotNull(savedImage);
        assertNotNull(savedImage.getId());

        Optional<HotelImage> foundImage = hotelImageRepository.findById(savedImage.getId());
        assertTrue(foundImage.isPresent());
        assertEquals(savedImage.getId(), foundImage.get().getId());
        assertEquals("image1.jpg", foundImage.get().getUrl());
        assertEquals(testHotel.getId(), foundImage.get().getHotel().getId());
    }

    @Test
    void testFindAll() {
        hotelImageRepository.save(image1);
        hotelImageRepository.save(image2);
        List<HotelImage> images = hotelImageRepository.findAll();
        assertEquals(2, images.size());
        assertTrue(images.stream().anyMatch(i -> i.getUrl().equals("image1.jpg")));
        assertTrue(images.stream().anyMatch(i -> i.getUrl().equals("image2.jpg")));
    }

    @Test
    void testDeleteImage() {
        HotelImage savedImage = hotelImageRepository.save(image1);
        assertNotNull(savedImage.getId());
        hotelImageRepository.delete(savedImage);
        Optional<HotelImage> deletedImage = hotelImageRepository.findById(savedImage.getId());
        assertFalse(deletedImage.isPresent());
    }

    @Test
    void testCountImages() {
        assertEquals(0, hotelImageRepository.count());
        hotelImageRepository.save(image1);
        hotelImageRepository.save(image2);
        assertEquals(2, hotelImageRepository.count());
    }

    @Test
    void testFindByHotelId() {
        hotelImageRepository.save(image1);
        hotelImageRepository.save(image2);
        List<HotelImageDTO> dtos = hotelImageRepository.findByHotelId(testHotel.getId());
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertTrue(dtos.stream().anyMatch(dto -> dto.getUrl().equals("image1.jpg")));
        assertTrue(dtos.stream().anyMatch(dto -> dto.getUrl().equals("image2.jpg")));
    }

    @Test
    void testDeleteByHotelId() {
        hotelImageRepository.save(image1);
        hotelImageRepository.save(image2);
        hotelImageRepository.deleteByHotelId(testHotel.getId());
        List<HotelImage> images = hotelImageRepository.findAll();
        assertTrue(images.isEmpty());
    }
} 