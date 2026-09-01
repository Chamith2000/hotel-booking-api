package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
import com.zerocode.hotelPackagesApi.exception.NotCreatedException;
import com.zerocode.hotelPackagesApi.exception.SuperDealNotFoundException;
import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.model.SuperDeal;
import com.zerocode.hotelPackagesApi.repository.PackageRepository;
import com.zerocode.hotelPackagesApi.repository.SuperDealsRepository;
import com.zerocode.hotelPackagesApi.service.SuperDealsService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SuperDealsServiceImpl implements SuperDealsService {

    private PackageRepository packageRepository;
    private SuperDealsRepository superDealsRepository;

    @Override
    @Transactional
    public void create(Long packageId,CreateSuperDealsRequest createSuperDealsRequest) throws NotCreatedException {
        HotelPackage hotelPackage = packageRepository.findById(packageId).orElseThrow(() -> new NotCreatedException(String.format("Package with id %s not found", packageId)));
        SuperDeal superDeal = new SuperDeal();
        superDeal.setStartDate(createSuperDealsRequest.getStartDate());
        superDeal.setEndDate(createSuperDealsRequest.getEndDate());
        superDeal.setDiscountedPrice(createSuperDealsRequest.getDiscountedPrice());
        superDeal.setHotelPackage(packageRepository.findById(packageId).get());
        superDeal.setHotel(packageRepository.findById(packageId).get().getHotel());
        superDealsRepository.save(superDeal);

    }

    @Override
    public List<SuperDeal> findAll() {
        return superDealsRepository.findAll();
    }

    @Override
    public SuperDeal findById(Long superDealId) throws SuperDealNotFoundException {
        SuperDeal superDeal = superDealsRepository.findById(superDealId).orElseThrow(()-> new SuperDealNotFoundException("SuperDeal not found with id " + superDealId));
        return superDeal;
    }

    @Transactional
    @Override
    public void deleteById(Long superDealId) throws SuperDealNotFoundException {
        SuperDeal superDeal = superDealsRepository.findById(superDealId)
                .orElseThrow(() -> new SuperDealNotFoundException("SuperDeal not found with id " + superDealId));
        if (superDeal.getHotelPackage() != null) {
            HotelPackage hotelPackage = superDeal.getHotelPackage();
            hotelPackage.setSuperDeals(null);
            packageRepository.save(hotelPackage);
        }
        superDealsRepository.delete(superDeal);
    }

}
