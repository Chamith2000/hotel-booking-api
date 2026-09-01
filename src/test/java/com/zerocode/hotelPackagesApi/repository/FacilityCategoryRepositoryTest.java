package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.FacilityCategory;
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
public class FacilityCategoryRepositoryTest {

    @Autowired
    private FacilityCategoryRepository facilityCategoryRepository;

    private FacilityCategory category1;
    private FacilityCategory category2;

    @BeforeEach
    void setup() {
        facilityCategoryRepository.deleteAll();

        category1 = new FacilityCategory();
        category1.setCategoryName("Pool");

        category2 = new FacilityCategory();
        category2.setCategoryName("Gym");
    }

    @Test
    void testSaveAndFindById() {
        FacilityCategory savedCategory = facilityCategoryRepository.save(category1);
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId());

        Optional<FacilityCategory> foundCategory = facilityCategoryRepository.findById(savedCategory.getId());
        assertTrue(foundCategory.isPresent());
        assertEquals(savedCategory.getId(), foundCategory.get().getId());
        assertEquals("Pool", foundCategory.get().getCategoryName());
    }

    @Test
    void testFindAll() {
        facilityCategoryRepository.save(category1);
        facilityCategoryRepository.save(category2);
        List<FacilityCategory> categories = facilityCategoryRepository.findAll();
        assertEquals(2, categories.size());
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryName().equals("Pool")));
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryName().equals("Gym")));
    }

    @Test
    void testDeleteCategory() {
        FacilityCategory savedCategory = facilityCategoryRepository.save(category1);
        assertNotNull(savedCategory.getId());
        facilityCategoryRepository.delete(savedCategory);
        Optional<FacilityCategory> deletedCategory = facilityCategoryRepository.findById(savedCategory.getId());
        assertFalse(deletedCategory.isPresent());
    }

    @Test
    void testCountCategories() {
        assertEquals(0, facilityCategoryRepository.count());
        facilityCategoryRepository.save(category1);
        facilityCategoryRepository.save(category2);
        assertEquals(2, facilityCategoryRepository.count());
    }

    @Test
    void testFindByCategoryName() {
        facilityCategoryRepository.save(category1);
        List<FacilityCategory> found = facilityCategoryRepository.findByCategoryName("Pool");
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals("Pool", found.get(0).getCategoryName());

        List<FacilityCategory> notFound = facilityCategoryRepository.findByCategoryName("Spa");
        assertNotNull(notFound);
        assertTrue(notFound.isEmpty());
    }
} 