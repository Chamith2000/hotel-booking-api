package com.zerocode.hotelPackagesApi.controller.request;

import lombok.Data;

@Data
public class CreateAdminRequest {
    private String userName;
    private String password;
    private String email;
    private String profilePhotoUrl;
}
