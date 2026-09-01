package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Role;
import com.zerocode.hotelPackagesApi.model.User;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEnabled(true);
        user1.setRole(Role.ADMIN);

        user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEnabled(false);
        user2.setRole(Role.CUSTOMER);
    }

    @Test
    void testSaveAndFindById() {
        User saved = userRepository.save(user1);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("user1", found.get().getUsername());
    }

    @Test
    void testFindAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    void testDeleteUser() {
        User saved = userRepository.save(user1);
        assertNotNull(saved.getId());
        userRepository.delete(saved);
        Optional<User> deleted = userRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCountUsers() {
        assertEquals(0, userRepository.count());
        userRepository.save(user1);
        userRepository.save(user2);
        assertEquals(2, userRepository.count());
    }

    @Test
    void testFindByUsername() {
        userRepository.save(user1);
        Optional<User> found = userRepository.findByUsername("user1");
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUsername());

        Optional<User> notFound = userRepository.findByUsername("notfound");
        assertFalse(notFound.isPresent());
    }
} 