package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.RoomType;
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
public class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private RoomType roomType1;
    private RoomType roomType2;

    @BeforeEach
    void setup() {
        roomTypeRepository.deleteAll();

        roomType1 = new RoomType();
        roomType1.setType("Deluxe");
        roomType1.setPrice(200.0);

        roomType2 = new RoomType();
        roomType2.setType("Suite");
        roomType2.setPrice(350.0);
    }

    @Test
    void testSaveAndFindById() {
        RoomType saved = roomTypeRepository.save(roomType1);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        Optional<RoomType> found = roomTypeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Deluxe", found.get().getType());
    }

    @Test
    void testFindAll() {
        roomTypeRepository.save(roomType1);
        roomTypeRepository.save(roomType2);
        List<RoomType> list = roomTypeRepository.findAll();
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(r -> r.getType().equals("Deluxe")));
        assertTrue(list.stream().anyMatch(r -> r.getType().equals("Suite")));
    }

    @Test
    void testDeleteRoomType() {
        RoomType saved = roomTypeRepository.save(roomType1);
        assertNotNull(saved.getId());
        roomTypeRepository.delete(saved);
        Optional<RoomType> deleted = roomTypeRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCountRoomTypes() {
        assertEquals(0, roomTypeRepository.count());
        roomTypeRepository.save(roomType1);
        roomTypeRepository.save(roomType2);
        assertEquals(2, roomTypeRepository.count());
    }

    @Test
    void testFindByType() {
        roomTypeRepository.save(roomType1);
        Optional<RoomType> found = roomTypeRepository.findByType("Deluxe");
        assertTrue(found.isPresent());
        assertEquals("Deluxe", found.get().getType());

        Optional<RoomType> notFound = roomTypeRepository.findByType("Penthouse");
        assertFalse(notFound.isPresent());
    }
} 