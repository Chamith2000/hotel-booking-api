package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelRoomType;
import com.zerocode.hotelPackagesApi.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRoomTypeRepository extends JpaRepository<HotelRoomType, Long> {
    Optional<HotelRoomType> findByHotelAndRoomType(Hotel hotel, RoomType roomType);
    List<HotelRoomType> findByHotel(Hotel hotel);
}
