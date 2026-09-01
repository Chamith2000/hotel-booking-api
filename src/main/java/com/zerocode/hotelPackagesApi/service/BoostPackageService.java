package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageList;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageListResponse;
import com.zerocode.hotelPackagesApi.exception.BoostPackageNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;

public interface BoostPackageService {
    void createBoostPackage(Long packageID, CreateBoostPackageRequestDTO createBoostPackageRequestDTO) throws PackageNotFoundException;
    BoostPackageList getBoostPackageById(Long boostPackageID) throws BoostPackageNotFoundException, PackageNotFoundException;
    void deleteBoostPackage(Long boostPackageID) throws BoostPackageNotFoundException;
    BoostPackageListResponse getBoostPackages() throws BoostPackageNotFoundException;
}
