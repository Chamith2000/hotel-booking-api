package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateLoyaltyPointRequest;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.LoyaltyPointNotFoundException;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;

public interface LoyaltyPointService {
    void  addLoyaltyPoint(Long customerId, CreateLoyaltyPointRequest createLoyaltyPointRequest)throws CustomerNotFoundException;
    void  updateLoyaltyPoint(Long id,LoyaltyPoint loyaltyPoint)throws CustomerNotFoundException;
    LoyaltyPoint getLoyaltyPointById(Long Id)throws CustomerNotFoundException;

}
