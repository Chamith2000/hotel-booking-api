package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.exception.*;

import java.util.List;

public interface PackageService {

    void createPackages(Long hotelId, CreatePackageRequestDTO createPackageRequestDTO) throws HotelNotFoundException;
    List<PackageListItem> findAll();
    PackageListItem findById(Long packageId) throws PackageNotFoundException;
    void deletePackageById(Long packageId) throws PackageNotFoundException;
    void updatePackageById(Long packageId, CreatePackageRequestDTO packageRequestDTO) throws PackageNotFoundException;
    List<PackageListItem> findByStatus(Boolean status);
    List<PackageListItem> findByHotelId(Long hotelId);
    int getPackagesCount();
    int getPackagesCountByStatus(Boolean status);
}
