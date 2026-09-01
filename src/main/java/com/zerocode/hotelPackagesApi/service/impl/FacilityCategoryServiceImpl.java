package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.response.FacilityResponse;
import com.zerocode.hotelPackagesApi.exception.FacilityAlreadyExistsException;
import com.zerocode.hotelPackagesApi.exception.FacilityNotFoundException;
import com.zerocode.hotelPackagesApi.model.FacilityCategory;
import com.zerocode.hotelPackagesApi.repository.FacilityCategoryRepository;
import com.zerocode.hotelPackagesApi.service.FacilityCategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityCategoryServiceImpl implements FacilityCategoryService {

    private final FacilityCategoryRepository facilityCategoryRepository;

    @Override
    public FacilityResponse getById(Long id) throws FacilityNotFoundException {
        FacilityCategory facility = facilityCategoryRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException("Facility not found with ID: " + id));

        return FacilityResponse.builder()
                .id(facility.getId())
                .categoryName(facility.getCategoryName())
                .build();
    }

    @Override
    public List<FacilityCategory> getByName(String categoryName) throws FacilityNotFoundException {
        List<FacilityCategory> facilityList = facilityCategoryRepository.findByCategoryName(categoryName);
        if (facilityList.isEmpty()) {
            throw new FacilityNotFoundException("No facilities found with name: " + categoryName);
        }
        return facilityList;
    }

    @Override
    public List<FacilityCategory> findAll() {
        return facilityCategoryRepository.findAll();
    }

    @Override
    public void add(FacilityCategory facility) throws FacilityAlreadyExistsException {
        if (!facilityCategoryRepository.findByCategoryName(facility.getCategoryName()).isEmpty()) {
            throw new FacilityAlreadyExistsException("Facility already exists with name: " + facility.getCategoryName());
        }
        facilityCategoryRepository.save(facility);
    }

    @Override
    public void update(Long id, FacilityCategory facility) throws FacilityNotFoundException {
        FacilityCategory existingFacility = facilityCategoryRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException("Facility not found with ID: " + id));

        existingFacility.setCategoryName(facility.getCategoryName());
        facilityCategoryRepository.save(existingFacility);
    }

    @Override
    public void delete(Long id) throws FacilityNotFoundException {
        FacilityCategory facility = facilityCategoryRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException("Facility not found with ID: " + id));
        facilityCategoryRepository.delete(facility);
    }
}