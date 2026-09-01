package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackageTypeRepository extends JpaRepository<PackageType,Long> {

    Optional<PackageType> findByName(String name);
}

