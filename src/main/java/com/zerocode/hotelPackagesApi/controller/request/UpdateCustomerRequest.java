package com.zerocode.hotelPackagesApi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerRequest {
    private String userName;
    private String password;
    private String email;
    private String profilePhotoUrl;

}
