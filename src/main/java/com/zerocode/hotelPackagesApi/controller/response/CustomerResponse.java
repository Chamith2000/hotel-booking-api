package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerResponse {
    private Long id;
    private String userName;
    private String password;
    private String email;
    private String profilePhotoUrl;
    private LoyaltyPoint loyaltyPoint;
}
