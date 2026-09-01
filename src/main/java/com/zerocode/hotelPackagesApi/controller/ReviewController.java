package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateReviewRequest;
import com.zerocode.hotelPackagesApi.controller.response.ReviewResponse;
import com.zerocode.hotelPackagesApi.exception.*;
import com.zerocode.hotelPackagesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping(value = "/customers/{customer-id}/hotels/{hotel-id}/reviews", headers = "X-Api-Version=v1")
    public void createReview(
            @PathVariable("customer-id") Long customerId,
            @PathVariable("hotel-id") Long hotelId,
            @RequestBody @Valid CreateReviewRequest createReviewRequest) throws CustomerNotFoundException, HotelNotFoundException {

        reviewService.createReview(customerId, hotelId, createReviewRequest);
    }

    @GetMapping(value = "/reviews/{review_id}", headers = "X-Api-Version=v1")
    public ReviewResponse getById(@PathVariable Long review_id) throws ReviewNotFoundException {
        return reviewService.findById(review_id);
    }
}
