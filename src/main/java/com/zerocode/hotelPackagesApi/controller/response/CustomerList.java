package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerList {
    private Long id;
    private String userName;
    private String email;
    private String profilePhotoUrl;
    private LoyaltyPoint loyaltyPoint;
}
