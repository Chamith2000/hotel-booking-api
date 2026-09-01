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
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Customer testCustomer;
    private Hotel testHotel;
    private Review testReview1;
    private Review testReview2;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        customerRepository.deleteAll();
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

        // Setup Customer
        testCustomer = new Customer();
        testCustomer.setUserName("testuser");
        testCustomer.setPassword("password");
        testCustomer.setEmail("test@customer.com");
        testCustomer = customerRepository.save(testCustomer);

        // Setup Review 1
        testReview1 = new Review();
        testReview1.setRating(5);
        testReview1.setComment("Great stay!");
        testReview1.setPostedDateAndTime(LocalDateTime.now());
        testReview1.setCustomer(testCustomer);
        testReview1.setHotel(testHotel);

        // Setup Review 2
        testReview2 = new Review();
        testReview2.setRating(3);
        testReview2.setComment("Average experience.");
        testReview2.setPostedDateAndTime(LocalDateTime.now());
        testReview2.setCustomer(testCustomer);
        testReview2.setHotel(testHotel);
    }

    @Test
    void testSaveAndFindById() {
        Review savedReview = reviewRepository.save(testReview1);
        assertNotNull(savedReview);
        assertNotNull(savedReview.getId());

        Optional<Review> foundReview = reviewRepository.findById(savedReview.getId());
        assertTrue(foundReview.isPresent());
        assertEquals(savedReview.getId(), foundReview.get().getId());
        assertEquals("Great stay!", foundReview.get().getComment());
        assertEquals(testCustomer.getId(), foundReview.get().getCustomer().getId());
        assertEquals(testHotel.getId(), foundReview.get().getHotel().getId());
    }

    @Test
    void testFindAll() {
        reviewRepository.save(testReview1);
        reviewRepository.save(testReview2);
        List<Review> reviews = reviewRepository.findAll();
        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().anyMatch(r -> r.getComment().equals("Great stay!")));
        assertTrue(reviews.stream().anyMatch(r -> r.getComment().equals("Average experience.")));
    }

    @Test
    void testDeleteReview() {
        Review savedReview = reviewRepository.save(testReview1);
        assertNotNull(savedReview.getId());
        reviewRepository.delete(savedReview);
        Optional<Review> deletedReview = reviewRepository.findById(savedReview.getId());
        assertFalse(deletedReview.isPresent());
    }

    @Test
    void testCountReviews() {
        assertEquals(0, reviewRepository.count());
        reviewRepository.save(testReview1);
        reviewRepository.save(testReview2);
        assertEquals(2, reviewRepository.count());
    }
}
