package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.controller.response.PackageListResponse;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.service.PackageService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PackageController {

    @Autowired
    private PackageService packageService;

    @RolesAllowed({"HOTEL"})
    @PostMapping(value = "/hotels/{hotel-id}/packages", headers = "X-Api-Version=v1")
    public void createPackage(@PathVariable("hotel-id") Long hotelId, @Valid @RequestBody CreatePackageRequestDTO packageRequestDTO) throws HotelNotFoundException {
        packageService.createPackages(hotelId, packageRequestDTO);
    }

    @RolesAllowed({"CUSTOMER", "HOTEL", "ADMIN"})
    @GetMapping(value = "/packages", headers = "X-Api-Version=v1")
    public PackageListResponse getAllPackages() {
        return new PackageListResponse(packageService.findAll());
    }

    @RolesAllowed({"CUSTOMER", "HOTEL", "ADMIN"})
    @GetMapping(value = "/packages/{package-id}", headers = "X-Api-Version=v1")
    public PackageListItem getPackageById(@PathVariable("package-id") Long packageId) throws PackageNotFoundException {
        return packageService.findById(packageId);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @DeleteMapping(value = "/packages/{package-id}", headers = "X-Api-Version=v1")
    public void deletePackageById(@PathVariable("package-id") Long packageId) throws PackageNotFoundException {
        packageService.deletePackageById(packageId);
    }

    @RolesAllowed({"HOTEL"})
    @PutMapping(value = "/packages/{package-id}", headers = "X-Api-Version=v1")
    public void updatePackageById(@PathVariable("package-id") Long packageId, @Valid @RequestBody CreatePackageRequestDTO packageRequestDTO) throws PackageNotFoundException {
        packageService.updatePackageById(packageId, packageRequestDTO);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(value = "/packages/findByStatus", headers = "X-Api-Version=v1")
    public PackageListResponse getPackagesByStatus(@RequestParam Boolean status) {
        return new PackageListResponse(packageService.findByStatus(status));
    }

    @RolesAllowed({"HOTEL", "CUSTOMER"})
    @GetMapping(value = "/hotels/{hotel-id}/packages", headers = "X-Api-Version=v1")
    public PackageListResponse getPackagesByHotelId(@PathVariable("hotel-id") Long hotelId) {
        return new PackageListResponse(packageService.findByHotelId(hotelId));
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping(value = "/packages-count", headers = "X-Api-Version=v1")
    public int getPackagesCount() {
        return packageService.getPackagesCount();
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping(value = "/packages-count/findByStatus", headers = "X-Api-Version=v1")
    public int getPackagesCountByStatus(@RequestParam Boolean status) {
        return packageService.getPackagesCountByStatus(status);
    }
}
