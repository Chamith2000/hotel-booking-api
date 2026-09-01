package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.HotelMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelMenuRepository extends JpaRepository<HotelMenu,Long> {

    List<HotelMenu> findByHotel_Id(Long hotelId);

    boolean existsByHotel_IdAndType(Long hotelId, String type);

}
