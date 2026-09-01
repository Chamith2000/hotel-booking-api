package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import com.zerocode.hotelPackagesApi.model.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImage, Long> {
    List<HotelImageDTO> findByHotelId(Long hotelId);
    void deleteByHotelId(Long hotelId);
}
