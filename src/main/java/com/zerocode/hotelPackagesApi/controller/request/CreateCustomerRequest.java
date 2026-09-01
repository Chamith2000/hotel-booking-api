package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.model.Review;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {
    private Long customerId;
    private String userName;
    private String password;
    private String email;
    private String profilePhotoUrl;
}
