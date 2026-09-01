package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
import com.zerocode.hotelPackagesApi.exception.NotCreatedException;
import com.zerocode.hotelPackagesApi.exception.SuperDealNotFoundException;
import com.zerocode.hotelPackagesApi.model.SuperDeal;

import java.util.List;

public interface SuperDealsService {
    void create(Long packageID,CreateSuperDealsRequest createSuperDealsRequest) throws NotCreatedException;
    List<SuperDeal> findAll();
    SuperDeal findById(Long superDealId) throws SuperDealNotFoundException;
    void deleteById(Long superDealId) throws SuperDealNotFoundException;
}
