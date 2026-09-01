package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.PackageController;
import com.zerocode.hotelPackagesApi.controller.request.CreateBoostPackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageList;
import com.zerocode.hotelPackagesApi.controller.response.BoostPackageListResponse;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.exception.BoostPackageNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.BoostPackage;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.repository.BoostPackageRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import com.zerocode.hotelPackagesApi.service.BoostPackageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BoostPackageServiceImpl implements BoostPackageService {
    private BoostPackageRepository boostPackageRepository;
    private PackageRepository packageRepository;
    private PackageController packageController;
    private PackageRepository hotelPackageRepository;

    @Override
    public void createBoostPackage(Long packageID, CreateBoostPackageRequestDTO createBoostPackageRequestDTO) throws PackageNotFoundException {
        HotelPackage hotelPackage = packageRepository.findById(packageID)
                .orElseThrow(() -> new PackageNotFoundException("Package not found with ID: " + packageID));

        BoostPackage boostPackage = new BoostPackage();
        boostPackage.setBoostedDate(createBoostPackageRequestDTO.getBoostedDate());
        boostPackage.setHotelPackage(hotelPackage);
        boostPackage.setHotel(hotelPackage.getHotel());
        boostPackageRepository.save(boostPackage);
    }

    @Override
    public BoostPackageList getBoostPackageById(Long boostPackageID) throws BoostPackageNotFoundException, PackageNotFoundException {
        BoostPackage boostPackage = boostPackageRepository.findById(boostPackageID)
                .orElseThrow(() -> new BoostPackageNotFoundException("Boost package not found with ID: " + boostPackageID));

        HotelPackage hotelPackage = boostPackage.getHotelPackage();
        if (hotelPackage == null) {
            throw new PackageNotFoundException("Associated hotel package not found for boost package ID: " + boostPackageID);
        }

        PackageListItem packageListItem = packageController.getPackageById(hotelPackage.getId());

        BoostPackageList boostPackageList = new BoostPackageList();
        boostPackageList.setId(boostPackage.getId());
        boostPackageList.setBoostedDate(boostPackage.getBoostedDate());
        boostPackageList.setName(packageListItem.getName());
        boostPackageList.setPrice(packageListItem.getPrice());
        boostPackageList.setDescription(packageListItem.getDescription());
        boostPackageList.setStartDate(packageListItem.getStartDate());
        boostPackageList.setEndDate(packageListItem.getEndDate());
        boostPackageList.setTermsAndCondition(packageListItem.getTermsAndCondition());
        boostPackageList.setVisitorCount(packageListItem.getVisitorCount());
        boostPackageList.setStatus(packageListItem.getStatus());
        boostPackageList.setGuestAdults(packageListItem.getGuestAdults());
        boostPackageList.setGuestChildren(packageListItem.getGuestChildren());
        boostPackageList.setImageUrls(packageListItem.getImageUrls());

        return boostPackageList;
    }

    @Override
    public void deleteBoostPackage(Long boostPackageID) throws BoostPackageNotFoundException {
        BoostPackage boostPackage = boostPackageRepository.findById(boostPackageID)
                .orElseThrow(() -> new BoostPackageNotFoundException("Boost package not found with ID: " + boostPackageID));

        HotelPackage hotelPackage = boostPackage.getHotelPackage();
        if (hotelPackage != null) {
            hotelPackage.setBoostPackage(null);
            hotelPackageRepository.save(hotelPackage);
        }

        boostPackageRepository.delete(boostPackage);
    }

    @Override
    public BoostPackageListResponse getBoostPackages() throws BoostPackageNotFoundException {
        List<BoostPackage> boostPackages = boostPackageRepository.findAll();

        if (boostPackages.isEmpty()) {
            throw new BoostPackageNotFoundException("No boost packages found");
        }

        List<BoostPackageList> boostPackageListItems = new ArrayList<>();

        for (BoostPackage boostPackage : boostPackages) {
            HotelPackage hotelPackage = boostPackage.getHotelPackage();
            if (hotelPackage == null) {
                throw new RuntimeException("Associated hotel package not found for boost package ID: " + boostPackage.getId());
            }

            PackageListItem packageListItem;
            try {
                packageListItem = packageController.getPackageById(hotelPackage.getId());
            } catch (PackageNotFoundException e) {
                throw new RuntimeException("Failed to fetch package details for ID: " + hotelPackage.getId(), e);
            }

            BoostPackageList boostPackageList = new BoostPackageList();
            boostPackageList.setId(boostPackage.getId());
            boostPackageList.setBoostedDate(boostPackage.getBoostedDate());
            boostPackageList.setName(packageListItem.getName());
            boostPackageList.setPrice(packageListItem.getPrice());
            boostPackageList.setDescription(packageListItem.getDescription());
            boostPackageList.setStartDate(packageListItem.getStartDate());
            boostPackageList.setEndDate(packageListItem.getEndDate());
            boostPackageList.setTermsAndCondition(packageListItem.getTermsAndCondition());
            boostPackageList.setVisitorCount(packageListItem.getVisitorCount());
            boostPackageList.setStatus(packageListItem.getStatus());
            boostPackageList.setGuestAdults(packageListItem.getGuestAdults());
            boostPackageList.setGuestChildren(packageListItem.getGuestChildren());
            boostPackageList.setImageUrls(packageListItem.getImageUrls());

            boostPackageListItems.add(boostPackageList);
        }

        return new BoostPackageListResponse(boostPackageListItems);
    }
}
