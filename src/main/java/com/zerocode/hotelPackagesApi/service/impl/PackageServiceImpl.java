package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageRequestDTO;
import com.zerocode.hotelPackagesApi.controller.response.PackageListItem;
import com.zerocode.hotelPackagesApi.exception.*;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.model.PackageImage;
import com.zerocode.hotelPackagesApi.model.PackageStatus;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import com.zerocode.hotelPackagesApi.service.PackageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PackageServiceImpl implements PackageService {
    private PackageRepository packageRepository;
    private HotelRepository hotelRepository;

    @Override
    @Transactional
    public void createPackages(Long hotelId, CreatePackageRequestDTO packageRequestDTO) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        HotelPackage hotelPackage = new HotelPackage();
        hotelPackage.setName(packageRequestDTO.getName());
        hotelPackage.setDescription(packageRequestDTO.getDescription());
        hotelPackage.setPrice(packageRequestDTO.getPrice());
        hotelPackage.setStartDate(packageRequestDTO.getStartDate());
        hotelPackage.setEndDate(packageRequestDTO.getEndDate());
        hotelPackage.setTermsAndCondition(packageRequestDTO.getTermsAndCondition());
        hotelPackage.setGuestCount(packageRequestDTO.getGuestCount());
        hotelPackage.setStatus(packageRequestDTO.getStatus());
        hotelPackage.setHotel(hotel);

        List<PackageImage> images = packageRequestDTO.getImages();
        for (PackageImage image : images) {
            image.setHotelPackage(hotelPackage);
        }
        hotelPackage.setPackageImages(images);
        hotelPackage.setPackageStatus(PackageStatus.PENDING);
        packageRepository.save(hotelPackage);
    }

    @Override
    public void deletePackageById(Long packageId) throws PackageNotFoundException {
        HotelPackage hotelPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new PackageNotFoundException("Package not found with ID: " + packageId));

        packageRepository.delete(hotelPackage);
    }

    @Override
    public void updatePackageById(Long packageId, CreatePackageRequestDTO packageRequestDTO) throws PackageNotFoundException {
        HotelPackage hotelPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new PackageNotFoundException("Package not found with ID: " + packageId));

        hotelPackage.setName(packageRequestDTO.getName());
        hotelPackage.setDescription(packageRequestDTO.getDescription());
        hotelPackage.setPrice(packageRequestDTO.getPrice());
        hotelPackage.setStartDate(packageRequestDTO.getStartDate());
        hotelPackage.setEndDate(packageRequestDTO.getEndDate());
        hotelPackage.setTermsAndCondition(packageRequestDTO.getTermsAndCondition());
        hotelPackage.setGuestCount(packageRequestDTO.getGuestCount());
        hotelPackage.setStatus(packageRequestDTO.getStatus());

        List<PackageImage> images = packageRequestDTO.getImages();
        for (PackageImage image : images) {
            image.setHotelPackage(hotelPackage);
        }
        hotelPackage.setPackageImages(images);

        packageRepository.save(hotelPackage);
    }

    @Override
    public List<PackageListItem> findByHotelId(Long hotelId) {
        return mapToPackageListItem(packageRepository.findByHotelId(hotelId));
    }

    @Override
    public List<PackageListItem> findAll() {
        return mapToPackageListItem(packageRepository.findAll());
    }

    @Override
    public PackageListItem findById(Long packageId) throws PackageNotFoundException {
        HotelPackage hotelPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new PackageNotFoundException("Package not found with ID: " + packageId));
        return convertToPackageListItem(hotelPackage);
    }

    @Override
    public List<PackageListItem> findByStatus(Boolean status) {
        return mapToPackageListItem(packageRepository.findByStatus(status));
    }

    private List<PackageListItem> mapToPackageListItem(List<HotelPackage> hotelPackages) {
        List<PackageListItem> packageList = new ArrayList<>();
        for (HotelPackage hotelPackage : hotelPackages) {
            packageList.add(convertToPackageListItem(hotelPackage));
        }
        return packageList;
    }

    private PackageListItem convertToPackageListItem(HotelPackage hotelPackage) {
        return new PackageListItem(hotelPackage);
    }

    @Override
    public int getPackagesCount() {
        int hotelPackageCount = (int) packageRepository.count();
        return hotelPackageCount;
    }

    @Override
    public int getPackagesCountByStatus(Boolean status) {
        int packagesCountByStatus = packageRepository.findByStatus(status).size();
        return packagesCountByStatus;
    }

}
