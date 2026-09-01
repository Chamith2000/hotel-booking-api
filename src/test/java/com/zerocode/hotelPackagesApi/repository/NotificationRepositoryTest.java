package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AdminRepository adminRepository;

    private Admin testAdmin;
    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
        adminRepository.deleteAll();

        testAdmin = new Admin();
        testAdmin.setUserName("admin1");
        testAdmin.setPassword("password1");
        testAdmin.setEmail("admin1@example.com");
        testAdmin.setProfilePhotoUrl("photo1.jpg");
        testAdmin = adminRepository.save(testAdmin);

        notification1 = new Notification();
        notification1.setMessage("Welcome!");
        notification1.setSentDateAndTime(LocalDateTime.now());
        notification1.setStatus(true);
        notification1.setRole(Role.ADMIN);
        notification1.setAdmin(testAdmin);

        notification2 = new Notification();
        notification2.setMessage("System update");
        notification2.setSentDateAndTime(LocalDateTime.now().minusDays(1));
        notification2.setStatus(false);
        notification2.setRole(Role.ADMIN);
        notification2.setAdmin(testAdmin);
    }

    @Test
    void testSaveAndFindById() {
        Notification saved = notificationRepository.save(notification1);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        Optional<Notification> found = notificationRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Welcome!", found.get().getMessage());
        assertEquals(testAdmin.getId(), found.get().getAdmin().getId());
    }

    @Test
    void testFindAll() {
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Welcome!")));
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("System update")));
    }

    @Test
    void testDeleteNotification() {
        Notification saved = notificationRepository.save(notification1);
        assertNotNull(saved.getId());
        notificationRepository.delete(saved);
        Optional<Notification> deleted = notificationRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCountNotifications() {
        assertEquals(0, notificationRepository.count());
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        assertEquals(2, notificationRepository.count());
    }
} 