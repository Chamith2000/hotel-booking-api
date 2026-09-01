package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateReviewRequest;
import com.zerocode.hotelPackagesApi.controller.response.ReviewResponse;
import com.zerocode.hotelPackagesApi.exception.*;
import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.Review;
import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.ReviewRepository;
import com.zerocode.hotelPackagesApi.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private CustomerRepository customerRepository;
    private HotelRepository hotelRepository;


    @Override
    public void createReview(Long customerId, Long hotelId, CreateReviewRequest createReviewRequest) throws CustomerNotFoundException, HotelNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer not found with Id" + customerId));

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        Review review = new Review();
        review.setRating(createReviewRequest.getRating());
        review.setComment(createReviewRequest.getComment());
        review.setPostedDateAndTime(createReviewRequest.getPostedDateAndTime());
        review.setCustomer(customer);
        review.setHotel(hotel);

        reviewRepository.save(review);

    }

    @Override
    public ReviewResponse findById(Long reviewId) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ReviewNotFoundException("Review not found with id " + reviewId));

        return ReviewResponse.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .postedDateAndTime(review.getPostedDateAndTime())
                .build();
    }
}
