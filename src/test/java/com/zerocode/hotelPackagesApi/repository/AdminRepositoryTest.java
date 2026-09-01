package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Admin;
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
public class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    private Admin testAdmin1;
    private Admin testAdmin2;

    @BeforeEach
    void setup() {
        adminRepository.deleteAll();

        testAdmin1 = new Admin();
        testAdmin1.setUserName("admin1");
        testAdmin1.setPassword("password1");
        testAdmin1.setEmail("admin1@example.com");
        testAdmin1.setProfilePhotoUrl("photo1.jpg");

        testAdmin2 = new Admin();
        testAdmin2.setUserName("admin2");
        testAdmin2.setPassword("password2");
        testAdmin2.setEmail("admin2@example.com");
        testAdmin2.setProfilePhotoUrl("photo2.jpg");
    }

    @Test
    void testSaveAndFindById() {
        Admin savedAdmin = adminRepository.save(testAdmin1);
        assertNotNull(savedAdmin);
        assertNotNull(savedAdmin.getId());

        Optional<Admin> foundAdmin = adminRepository.findById(savedAdmin.getId());
        assertTrue(foundAdmin.isPresent());
        assertEquals(savedAdmin.getId(), foundAdmin.get().getId());
        assertEquals("admin1", foundAdmin.get().getUserName());
        assertEquals("admin1@example.com", foundAdmin.get().getEmail());
    }

    @Test
    void testFindAll() {
        adminRepository.save(testAdmin1);
        adminRepository.save(testAdmin2);
        List<Admin> admins = adminRepository.findAll();
        assertEquals(2, admins.size());
        assertTrue(admins.stream().anyMatch(a -> a.getUserName().equals("admin1")));
        assertTrue(admins.stream().anyMatch(a -> a.getUserName().equals("admin2")));
    }

    @Test
    void testDeleteAdmin() {
        Admin savedAdmin = adminRepository.save(testAdmin1);
        assertNotNull(savedAdmin.getId());
        adminRepository.delete(savedAdmin);
        Optional<Admin> deletedAdmin = adminRepository.findById(savedAdmin.getId());
        assertFalse(deletedAdmin.isPresent());
    }

    @Test
    void testCountAdmins() {
        assertEquals(0, adminRepository.count());
        adminRepository.save(testAdmin1);
        adminRepository.save(testAdmin2);
        assertEquals(2, adminRepository.count());
    }

    @Test
    void testFindByEmail() {
        adminRepository.save(testAdmin1);
        Optional<Admin> foundAdmin = adminRepository.findByEmail("admin1@example.com");
        assertTrue(foundAdmin.isPresent());
        assertEquals("admin1", foundAdmin.get().getUserName());
        assertEquals("admin1@example.com", foundAdmin.get().getEmail());

        Optional<Admin> notFound = adminRepository.findByEmail("notfound@example.com");
        assertFalse(notFound.isPresent());
    }
} 