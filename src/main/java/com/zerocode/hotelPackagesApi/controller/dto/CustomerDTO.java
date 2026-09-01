package com.zerocode.hotelPackagesApi.controller.dto;

import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String userName;
    private String email;
    private String profilePhotoUrl;
    private LoyaltyPoint loyaltyPoint;

}
