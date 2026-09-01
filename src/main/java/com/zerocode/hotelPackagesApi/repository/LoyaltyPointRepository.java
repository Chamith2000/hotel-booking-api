package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {
    Optional<LoyaltyPoint> findByCount(double count);
}
