package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageList;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageListResponse;
import com.zerocode.hotelPackagesApi.exception.BoostPackageNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.service.BoostPackageService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class BoostPackageController {
    private BoostPackageService boostPackageService;

    @RolesAllowed({"HOTEL"})
    @PostMapping(value = "/packages/{package-id}/boost-packages", headers = "X-Api-Version=v1")
    public void create(@PathVariable("package-id")Long packageID,@RequestBody CreateBoostPackageRequestDTO createBoostPackageRequestDTO) throws PackageNotFoundException {
        boostPackageService.createBoostPackage(packageID,createBoostPackageRequestDTO);
    }

    @RolesAllowed({"HOTEL", "ADMIN", "CUSTOMER"})
    @GetMapping(value = "/boost-packages/{boost-package-id}", headers = "X-Api-Version=v1")
    public BoostPackageList getBoostPackageById(@PathVariable("boost-package-id")Long boostPackageID) throws BoostPackageNotFoundException, PackageNotFoundException {
        return boostPackageService.getBoostPackageById(boostPackageID);
    }

    @RolesAllowed({"CUSTOMER", "ADMIN"})
    @GetMapping(value = "/boost-packages", headers = "X-Api-Version=v1")
    public ResponseEntity<BoostPackageListResponse> getBoostPackages() throws BoostPackageNotFoundException {
        BoostPackageListResponse response = boostPackageService.getBoostPackages();
        return ResponseEntity.ok(response);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @DeleteMapping(value = "/boost-packages/{boost-package-id}", headers = "X-Api-Version=v1")
    public void deleteBoostPackage(@PathVariable("boost-package-id")Long boostPackageID) throws BoostPackageNotFoundException {
        boostPackageService.deleteBoostPackage(boostPackageID);
    }

}
