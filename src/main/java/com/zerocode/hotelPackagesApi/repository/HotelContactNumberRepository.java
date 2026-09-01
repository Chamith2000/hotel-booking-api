package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
import com.zerocode.hotelPackagesApi.model.HotelContactNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelContactNumberRepository extends JpaRepository<HotelContactNumber, Long> {
    List<HotelContactNumberDTO> findByHotelId(Long hotelId);

    void deleteByHotelId(Long hotelId);
}
