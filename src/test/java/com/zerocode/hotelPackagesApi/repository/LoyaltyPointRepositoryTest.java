package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LoyaltyPointRepositoryTest {

    @Autowired
    private LoyaltyPointRepository loyaltyPointRepository;

    private LoyaltyPoint point1;
    private LoyaltyPoint point2;

    @BeforeEach
    void setup() {
        loyaltyPointRepository.deleteAll();

        point1 = new LoyaltyPoint();
        point1.setCount(100.0);
        point1.setEarnedDate(LocalDate.now());

        point2 = new LoyaltyPoint();
        point2.setCount(200.0);
        point2.setEarnedDate(LocalDate.now().minusDays(1));
    }

    @Test
    void testSaveAndFindById() {
        LoyaltyPoint saved = loyaltyPointRepository.save(point1);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        Optional<LoyaltyPoint> found = loyaltyPointRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(100.0, found.get().getCount());
    }

    @Test
    void testFindAll() {
        loyaltyPointRepository.save(point1);
        loyaltyPointRepository.save(point2);
        List<LoyaltyPoint> points = loyaltyPointRepository.findAll();
        assertEquals(2, points.size());
        assertTrue(points.stream().anyMatch(p -> p.getCount() == 100.0));
        assertTrue(points.stream().anyMatch(p -> p.getCount() == 200.0));
    }

    @Test
    void testDeleteLoyaltyPoint() {
        LoyaltyPoint saved = loyaltyPointRepository.save(point1);
        assertNotNull(saved.getId());
        loyaltyPointRepository.delete(saved);
        Optional<LoyaltyPoint> deleted = loyaltyPointRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCountLoyaltyPoints() {
        assertEquals(0, loyaltyPointRepository.count());
        loyaltyPointRepository.save(point1);
        loyaltyPointRepository.save(point2);
        assertEquals(2, loyaltyPointRepository.count());
    }

    @Test
    void testFindByCount() {
        loyaltyPointRepository.save(point1);
        Optional<LoyaltyPoint> found = loyaltyPointRepository.findByCount(100.0);
        assertTrue(found.isPresent());
        assertEquals(100.0, found.get().getCount());

        Optional<LoyaltyPoint> notFound = loyaltyPointRepository.findByCount(999.0);
        assertFalse(notFound.isPresent());
    }
} 