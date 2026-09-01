package com.zerocode.hotelPackagesApi.controller.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}