package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
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
public class HotelContactNumberRepositoryTest {

    @Autowired
    private HotelContactNumberRepository hotelContactNumberRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel testHotel;
    private HotelContactNumber contact1;
    private HotelContactNumber contact2;

    @BeforeEach
    void setup() {
        hotelContactNumberRepository.deleteAll();
        hotelRepository.deleteAll();

        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        testHotel = hotelRepository.save(testHotel);

        contact1 = new HotelContactNumber();
        contact1.setNumber("123-456-7890");
        contact1.setHotel(testHotel);

        contact2 = new HotelContactNumber();
        contact2.setNumber("987-654-3210");
        contact2.setHotel(testHotel);
    }

    @Test
    void testSaveAndFindById() {
        HotelContactNumber savedContact = hotelContactNumberRepository.save(contact1);
        assertNotNull(savedContact);
        assertNotNull(savedContact.getId());

        Optional<HotelContactNumber> foundContact = hotelContactNumberRepository.findById(savedContact.getId());
        assertTrue(foundContact.isPresent());
        assertEquals(savedContact.getId(), foundContact.get().getId());
        assertEquals("123-456-7890", foundContact.get().getNumber());
        assertEquals(testHotel.getId(), foundContact.get().getHotel().getId());
    }

    @Test
    void testFindAll() {
        hotelContactNumberRepository.save(contact1);
        hotelContactNumberRepository.save(contact2);
        List<HotelContactNumber> contacts = hotelContactNumberRepository.findAll();
        assertEquals(2, contacts.size());
        assertTrue(contacts.stream().anyMatch(c -> c.getNumber().equals("123-456-7890")));
        assertTrue(contacts.stream().anyMatch(c -> c.getNumber().equals("987-654-3210")));
    }

    @Test
    void testDeleteContact() {
        HotelContactNumber savedContact = hotelContactNumberRepository.save(contact1);
        assertNotNull(savedContact.getId());
        hotelContactNumberRepository.delete(savedContact);
        Optional<HotelContactNumber> deletedContact = hotelContactNumberRepository.findById(savedContact.getId());
        assertFalse(deletedContact.isPresent());
    }

    @Test
    void testCountContacts() {
        assertEquals(0, hotelContactNumberRepository.count());
        hotelContactNumberRepository.save(contact1);
        hotelContactNumberRepository.save(contact2);
        assertEquals(2, hotelContactNumberRepository.count());
    }

    @Test
    void testFindByHotelId() {
        hotelContactNumberRepository.save(contact1);
        hotelContactNumberRepository.save(contact2);
        List<HotelContactNumberDTO> dtos = hotelContactNumberRepository.findByHotelId(testHotel.getId());
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertTrue(dtos.stream().anyMatch(dto -> dto.getNumber().equals("123-456-7890")));
        assertTrue(dtos.stream().anyMatch(dto -> dto.getNumber().equals("987-654-3210")));
    }

    @Test
    void testDeleteByHotelId() {
        hotelContactNumberRepository.save(contact1);
        hotelContactNumberRepository.save(contact2);
        hotelContactNumberRepository.deleteByHotelId(testHotel.getId());
        List<HotelContactNumber> contacts = hotelContactNumberRepository.findAll();
        assertTrue(contacts.isEmpty());
    }
} 