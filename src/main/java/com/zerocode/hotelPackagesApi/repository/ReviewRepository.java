package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewRepository extends JpaRepository <Review,Long> {

//    List<Review> findByCustomerId(Long customerId);
}
