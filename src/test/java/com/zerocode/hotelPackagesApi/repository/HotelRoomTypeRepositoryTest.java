package com.zerocode.hotelPackagesApi.repository;

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
public class HotelRoomTypeRepositoryTest {

    @Autowired
    private HotelRoomTypeRepository hotelRoomTypeRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Hotel testHotel;
    private RoomType roomType1;
    private RoomType roomType2;
    private HotelRoomType hotelRoomType1;
    private HotelRoomType hotelRoomType2;

    @BeforeEach
    void setup() {
        hotelRoomTypeRepository.deleteAll();
        hotelRepository.deleteAll();
        roomTypeRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.save(testHotel);

        roomType1 = new RoomType();
        roomType1.setType("Deluxe");
        roomType1.setPrice(200.0);
        roomType1 = roomTypeRepository.save(roomType1);

        roomType2 = new RoomType();
        roomType2.setType("Suite");
        roomType2.setPrice(350.0);
        roomType2 = roomTypeRepository.save(roomType2);

        hotelRoomType1 = new HotelRoomType();
        hotelRoomType1.setHotel(testHotel);
        hotelRoomType1.setRoomType(roomType1);
        hotelRoomType1.setPrice(220.0);

        hotelRoomType2 = new HotelRoomType();
        hotelRoomType2.setHotel(testHotel);
        hotelRoomType2.setRoomType(roomType2);
        hotelRoomType2.setPrice(370.0);
    }

    @Test
    void testSaveAndFindById() {
        HotelRoomType saved = hotelRoomTypeRepository.save(hotelRoomType1);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        Optional<HotelRoomType> found = hotelRoomTypeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Deluxe", found.get().getRoomType().getType());
        assertEquals(testHotel.getId(), found.get().getHotel().getId());
    }

    @Test
    void testFindAll() {
        hotelRoomTypeRepository.save(hotelRoomType1);
        hotelRoomTypeRepository.save(hotelRoomType2);
        List<HotelRoomType> list = hotelRoomTypeRepository.findAll();
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(h -> h.getRoomType().getType().equals("Deluxe")));
        assertTrue(list.stream().anyMatch(h -> h.getRoomType().getType().equals("Suite")));
    }

    @Test
    void testDeleteHotelRoomType() {
        HotelRoomType saved = hotelRoomTypeRepository.save(hotelRoomType1);
        assertNotNull(saved.getId());
        hotelRoomTypeRepository.delete(saved);
        Optional<HotelRoomType> deleted = hotelRoomTypeRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCountHotelRoomTypes() {
        assertEquals(0, hotelRoomTypeRepository.count());
        hotelRoomTypeRepository.save(hotelRoomType1);
        hotelRoomTypeRepository.save(hotelRoomType2);
        assertEquals(2, hotelRoomTypeRepository.count());
    }

    @Test
    void testFindByHotel() {
        hotelRoomTypeRepository.save(hotelRoomType1);
        hotelRoomTypeRepository.save(hotelRoomType2);
        List<HotelRoomType> list = hotelRoomTypeRepository.findByHotel(testHotel);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(h -> h.getRoomType().getType().equals("Deluxe")));
        assertTrue(list.stream().anyMatch(h -> h.getRoomType().getType().equals("Suite")));
    }

    @Test
    void testFindByHotelAndRoomType() {
        hotelRoomTypeRepository.save(hotelRoomType1);
        hotelRoomTypeRepository.save(hotelRoomType2);
        Optional<HotelRoomType> found = hotelRoomTypeRepository.findByHotelAndRoomType(testHotel, roomType2);
        assertTrue(found.isPresent());
        assertEquals("Suite", found.get().getRoomType().getType());
        assertEquals(testHotel.getId(), found.get().getHotel().getId());
    }
} 