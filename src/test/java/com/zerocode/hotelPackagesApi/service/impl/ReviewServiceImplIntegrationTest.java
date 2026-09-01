//package com.zerocode.hotelPackagesApi.service.impl;
//
//import com.zerocode.hotelPackagesApi.controller.request.CreateReviewRequest;
//import com.zerocode.hotelPackagesApi.controller.response.ReviewResponse;
//import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
//import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
//import com.zerocode.hotelPackagesApi.exception.ReviewNotFoundException;
//import com.zerocode.hotelPackagesApi.model.Customer;
//import com.zerocode.hotelPackagesApi.model.Hotel;
//import com.zerocode.hotelPackagesApi.model.Review;
//import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
//import com.zerocode.hotelPackagesApi.repository.HotelRepository;
//import com.zerocode.hotelPackagesApi.repository.ReviewRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class ReviewServiceImplIntegrationTest {
//
//    @Autowired
//    private ReviewServiceImpl reviewService;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private HotelRepository hotelRepository;
//
//    private Customer customer;
//    private Hotel hotel;
//
//    @BeforeEach
//    void setUp() {
//        reviewRepository.deleteAll();
//        customerRepository.deleteAll();
//        hotelRepository.deleteAll();
//
//        customer = new Customer();
//        customer.setUserName("John Doe");
//        customer = customerRepository.save(customer);
//
//        hotel = new Hotel();
//        hotel.setName("Grand Hotel");
//        hotel = hotelRepository.save(hotel);
//    }
//
//    @Test
//    public void testCreateReview_Success() {
//        CreateReviewRequest createReviewRequest = new CreateReviewRequest();
//        createReviewRequest.setRating(5);
//        createReviewRequest.setComment("Great service!");
//        createReviewRequest.setPostedDateAndTime(LocalDateTime.now());
//
//        assertDoesNotThrow(() -> reviewService.createReview(customer.getId(), hotel.getId(), createReviewRequest));
//
//        Optional<Review> savedReview = reviewRepository.findAll().stream().findFirst();
//        assertTrue(savedReview.isPresent());
//        assertEquals("Great service!", savedReview.get().getComment());
//        assertEquals(5, savedReview.get().getRating());
//    }
//
//    @Test
//    public void testCreateReview_CustomerNotFound() {
//        CreateReviewRequest createReviewRequest = new CreateReviewRequest();
//        createReviewRequest.setRating(4);
//        createReviewRequest.setComment("Nice hotel!");
//
//        assertThrows(CustomerNotFoundException.class,
//                () -> reviewService.createReview(99L, hotel.getId(), createReviewRequest));
//    }
//
//    @Test
//    public void testCreateReview_HotelNotFound() {
//        CreateReviewRequest createReviewRequest = new CreateReviewRequest();
//        createReviewRequest.setRating(3);
//        createReviewRequest.setComment("Average experience");
//
//        assertThrows(HotelNotFoundException.class,
//                () -> reviewService.createReview(customer.getId(), 99L, createReviewRequest));
//    }
//
//    @Test
//    public void testFindById_Success() {
//        Review review = new Review();
//        review.setRating(5);
//        review.setComment("Amazing place!");
//        review.setPostedDateAndTime(LocalDateTime.now());
//        review.setCustomer(customer);
//        review.setHotel(hotel);
//
//        reviewRepository.save(review);
//        reviewRepository.flush();
//
//        Review finalReview = review;
//        ReviewResponse response = assertDoesNotThrow(() -> reviewService.findById(finalReview.getId()));
//
//        assertNotNull(response);
//        assertEquals(finalReview.getId(), response.getId());
//        assertEquals("Amazing place!", response.getComment());
//    }
//
//    @Test
//    public void testFindById_ReviewNotFound() {
//        assertThrows(ReviewNotFoundException.class, () -> reviewService.findById(999L));
//    }
//}
//
