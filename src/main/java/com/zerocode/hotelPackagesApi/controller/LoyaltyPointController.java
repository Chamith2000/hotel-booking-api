package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateLoyaltyPointRequest;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.LoyaltyPointNotFoundException;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import com.zerocode.hotelPackagesApi.service.LoyaltyPointService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LoyaltyPointController {
    private LoyaltyPointService loyaltyPointService;

    @RolesAllowed({"ADMIN"})
    @PostMapping(value = "/customers/{customer_id}/loyalty-points",headers = "X-Api-Version=v1")
    public void createLoyaltyPoint(@PathVariable Long customer_id, @RequestBody CreateLoyaltyPointRequest createLoyaltyPointRequest) throws CustomerNotFoundException {
        loyaltyPointService.addLoyaltyPoint(customer_id, createLoyaltyPointRequest);
    }

    @RolesAllowed({"CUSTOMER", "ADMIN"})
    @GetMapping(value = "/loyalty-points/{customer_id}",headers = "X-Api-Version=v1")
    public LoyaltyPoint getLoyaltyPointById(@PathVariable Long customer_id) throws CustomerNotFoundException {
        return loyaltyPointService.getLoyaltyPointById(customer_id);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping(value = "/loyalty-points/{customer_id}",headers = "X-Api-Version=v1")
    public void updateLoyaltyPoint(LoyaltyPoint loyaltyPoint, @PathVariable Long customer_id) throws CustomerNotFoundException {
        loyaltyPointService.updateLoyaltyPoint(customer_id, loyaltyPoint);
    }
}
