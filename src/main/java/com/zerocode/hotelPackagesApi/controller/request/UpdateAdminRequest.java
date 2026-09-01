package com.zerocode.hotelPackagesApi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdminRequest {
    private String userName;
    private String email;
    private String profilePhotoUrl;
}
