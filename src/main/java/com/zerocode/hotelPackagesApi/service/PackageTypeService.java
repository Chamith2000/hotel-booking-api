package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeList;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeListResponse;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotFoundException;

public interface PackageTypeService {
    PackageTypeListResponse getAll();
    PackageTypeList getById(Long id) throws PackageTypeNotFoundException;
    void delete(Long id)throws PackageTypeNotFoundException;
    void create(CreatePackageTypeRequest createNewPackageTypeDTO)throws PackageTypeNotCreatedException;
    void update(Long packageTypeId, UpdatePackageTypeRequest updatePackageTypeRequest)throws PackageTypeNotFoundException;
}
