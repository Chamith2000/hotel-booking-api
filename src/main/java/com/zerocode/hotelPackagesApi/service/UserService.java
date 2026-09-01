package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.AuthRequest;
import com.zerocode.hotelPackagesApi.exception.UserAlreadyExistsException;

public interface UserService {
    void register(AuthRequest authRequest) throws UserAlreadyExistsException;
}
