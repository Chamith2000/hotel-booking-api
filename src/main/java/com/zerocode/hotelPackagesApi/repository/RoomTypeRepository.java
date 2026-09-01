package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Optional<RoomType> findByType(String type);
}
