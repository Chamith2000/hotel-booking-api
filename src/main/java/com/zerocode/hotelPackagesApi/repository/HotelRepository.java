package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByEmail(String email);
    List<Hotel> findByStatus(Boolean status);
    List<Hotel> findByApprovalStatus(ApprovalStatus pending);
}
