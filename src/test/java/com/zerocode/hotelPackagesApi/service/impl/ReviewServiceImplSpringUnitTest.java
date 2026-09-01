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
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class ReviewServiceImplSpringUnitTest {
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private HotelRepository hotelRepository;
//
//    @InjectMocks
//    private ReviewServiceImpl reviewService;
//
//    private Customer customer;
//    private Hotel hotel;
//    private Review review;
//    private CreateReviewRequest createReviewRequest;
//
//    @BeforeEach
//    void setUp() {
//        customer = new Customer();
//        customer.setId(1L);
//
//        hotel = new Hotel();
//        hotel.setId(1L);
//
//        review = new Review();
//        review.setId(1L);
//        review.setRating(5);
//        review.setComment("Excellent service");
//        review.setPostedDateAndTime(LocalDateTime.now());
//        review.setCustomer(customer);
//        review.setHotel(hotel);
//
//        createReviewRequest = new CreateReviewRequest();
//        createReviewRequest.setRating(5);
//        createReviewRequest.setComment("Excellent service");
//        createReviewRequest.setPostedDateAndTime(LocalDateTime.now());
//    }
//
//    @Test
//    void testCreateReview_Success() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
//        when(reviewRepository.save(any(Review.class))).thenReturn(review);
//
//        assertDoesNotThrow(() -> reviewService.createReview(1L, 1L, createReviewRequest));
//        verify(reviewRepository, times(1)).save(any(Review.class));
//    }
//
//    @Test
//    void testCreateReview_CustomerNotFound() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(CustomerNotFoundException.class, () -> reviewService.createReview(1L, 1L, createReviewRequest));
//    }
//
//    @Test
//    void testCreateReview_HotelNotFound() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(HotelNotFoundException.class, () -> reviewService.createReview(1L, 1L, createReviewRequest));
//    }
//
//    @Test
//    void testFindById_Success() {
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//
//        assertDoesNotThrow(() -> {
//            ReviewResponse response = reviewService.findById(1L);
//            assertNotNull(response);
//            assertEquals(1L, response.getId());
//            assertEquals("Excellent service", response.getComment());
//        });
//    }
//
//    @Test
//    void testFindById_ReviewNotFound() {
//        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ReviewNotFoundException.class, () -> reviewService.findById(1L));
//    }
//}