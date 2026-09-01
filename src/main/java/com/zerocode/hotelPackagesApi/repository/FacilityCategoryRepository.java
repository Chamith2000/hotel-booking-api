package com.zerocode.hotelPackagesApi.repository;
import com.zerocode.hotelPackagesApi.model.FacilityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacilityCategoryRepository extends JpaRepository<FacilityCategory, Long> {
    List<FacilityCategory> findByCategoryName(String categoryName);
}