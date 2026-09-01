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
public class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private User testUser;
    private Hotel testHotel;
    private Authority testAuthority1;
    private Authority testAuthority2;

    @BeforeEach
    void setup() {
        authorityRepository.deleteAll();
        userRepository.deleteAll();
        hotelRepository.deleteAll();

        // Setup Hotel
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setEmail("test@hotel.com");
        testHotel.setPassword("password");
        testHotel.setStatus(true);
        testHotel.setApprovalStatus(ApprovalStatus.APPROVED);
        Address address = new Address();
        address.setStreet("123 Test St");
        testHotel.setAddress(address);
        testHotel = hotelRepository.save(testHotel);

        // Setup User
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser.setRole(Role.ADMIN);
        testUser = userRepository.save(testUser);

        // Setup Authority 1
        testAuthority1 = new Authority();
        testAuthority1.setAuthority("ROLE_ADMIN");
        testAuthority1.setRole(Role.ADMIN);
        testAuthority1.setUser(testUser);
        testAuthority1.setHotel(testHotel);

        // Setup Authority 2
        testAuthority2 = new Authority();
        testAuthority2.setAuthority("ROLE_HOTEL");
        testAuthority2.setRole(Role.HOTEL);
        testAuthority2.setUser(testUser);
        testAuthority2.setHotel(testHotel);
    }

    @Test
    void testSaveAndFindById() {
        Authority savedAuthority = authorityRepository.save(testAuthority1);
        assertNotNull(savedAuthority);
        assertNotNull(savedAuthority.getId());

        Optional<Authority> foundAuthority = authorityRepository.findById(savedAuthority.getId());
        assertTrue(foundAuthority.isPresent());
        assertEquals(savedAuthority.getId(), foundAuthority.get().getId());
        assertEquals("ROLE_ADMIN", foundAuthority.get().getAuthority());
        assertEquals(Role.ADMIN, foundAuthority.get().getRole());
        assertEquals(testUser.getId(), foundAuthority.get().getUser().getId());
        assertEquals(testHotel.getId(), foundAuthority.get().getHotel().getId());
    }

    @Test
    void testFindAll() {
        authorityRepository.save(testAuthority1);
        authorityRepository.save(testAuthority2);
        List<Authority> authorities = authorityRepository.findAll();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_HOTEL")));
    }

    @Test
    void testDeleteAuthority() {
        Authority savedAuthority = authorityRepository.save(testAuthority1);
        assertNotNull(savedAuthority.getId());
        authorityRepository.delete(savedAuthority);
        Optional<Authority> deletedAuthority = authorityRepository.findById(savedAuthority.getId());
        assertFalse(deletedAuthority.isPresent());
    }

    @Test
    void testCountAuthorities() {
        assertEquals(0, authorityRepository.count());
        authorityRepository.save(testAuthority1);
        authorityRepository.save(testAuthority2);
        assertEquals(2, authorityRepository.count());
    }
} 