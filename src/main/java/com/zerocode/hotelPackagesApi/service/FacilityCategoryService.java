package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.response.FacilityResponse;
import com.zerocode.hotelPackagesApi.exception.FacilityAlreadyExistsException;
import com.zerocode.hotelPackagesApi.exception.FacilityNotFoundException;
import com.zerocode.hotelPackagesApi.model.FacilityCategory;

import java.util.List;

public interface FacilityCategoryService {
    FacilityResponse getById(Long id) throws FacilityNotFoundException;
    List<FacilityCategory> getByName(String categoryName) throws FacilityNotFoundException;
    List<FacilityCategory> findAll();
    void add(FacilityCategory facility) throws FacilityAlreadyExistsException;
    void update(Long id, FacilityCategory facility) throws FacilityNotFoundException;
    void delete(Long id) throws FacilityNotFoundException;
}