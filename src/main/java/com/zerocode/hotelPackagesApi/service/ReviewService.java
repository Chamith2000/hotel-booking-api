package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateReviewRequest;
import com.zerocode.hotelPackagesApi.controller.response.ReviewResponse;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.ReviewNotFoundException;

public interface ReviewService {
    void createReview(Long customerId, Long hotelId, CreateReviewRequest createReviewRequest) throws CustomerNotFoundException, HotelNotFoundException;
    ReviewResponse findById(Long reviewId) throws ReviewNotFoundException;
}
