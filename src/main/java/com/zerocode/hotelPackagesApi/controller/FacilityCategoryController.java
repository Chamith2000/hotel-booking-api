package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.response.FacilityList;
import com.zerocode.hotelPackagesApi.controller.response.FacilityListResponse;
import com.zerocode.hotelPackagesApi.controller.response.FacilityResponse;
import com.zerocode.hotelPackagesApi.exception.FacilityAlreadyExistsException;
import com.zerocode.hotelPackagesApi.exception.FacilityNotFoundException;
import com.zerocode.hotelPackagesApi.model.FacilityCategory;
import com.zerocode.hotelPackagesApi.service.FacilityCategoryService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityCategoryController {

    private final FacilityCategoryService facilityCategoryService;

    @RolesAllowed({"HOTEL", "ADMIN"})
    @PostMapping(headers = "X-Api-Version=v1")
    public void create(@RequestBody FacilityCategory facility) throws FacilityAlreadyExistsException {
        facilityCategoryService.add(facility);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(headers = "X-Api-Version=v1")
    public FacilityListResponse getAll() {
        List<FacilityCategory> facilities = facilityCategoryService.findAll();
        var facilityList = facilities.stream()
                .map(facility -> FacilityList.builder()
                        .id(facility.getId())
                        .category(facility.getCategoryName())
                        .build())
                .toList();

        return FacilityListResponse.builder().facilities(facilityList).build();
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(value = "/{facility_id}", headers = "X-Api-Version=v1")
    public FacilityResponse getById(@PathVariable Long facility_id) throws FacilityNotFoundException {
        return facilityCategoryService.getById(facility_id);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping(value = "/{facility_id}", headers = "X-Api-Version=v1")
    public void update(@PathVariable Long facility_id, @RequestBody FacilityCategory facility) throws FacilityNotFoundException {
        facilityCategoryService.update(facility_id, facility);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(value = "/search", headers = "X-Api-Version=v1")
    public FacilityListResponse getAllByName(@RequestParam(value = "name") String name) throws FacilityNotFoundException {
        List<FacilityCategory> facilities = facilityCategoryService.getByName(name);
        var facilityList = facilities.stream()
                .map(facility -> FacilityList.builder()
                        .id(facility.getId())
                        .category(facility.getCategoryName())
                        .build())
                .toList();

        return FacilityListResponse.builder().facilities(facilityList).build();
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping(value = "/{facility_id}", headers = "X-Api-Version=v1")
    public void delete(@PathVariable Long facility_id) throws FacilityNotFoundException {
        facilityCategoryService.delete(facility_id);
    }
}