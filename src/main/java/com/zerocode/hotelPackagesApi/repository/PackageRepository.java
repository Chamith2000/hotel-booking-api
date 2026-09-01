package com.zerocode.hotelPackagesApi.repository;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackageRepository extends JpaRepository<HotelPackage, Long> {
    List<HotelPackage> findByStatus(Boolean status);
    List<HotelPackage> findByHotelId(Long hotelId);
}
