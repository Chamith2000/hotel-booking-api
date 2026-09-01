package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateAdminRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateAdminRequest;
import com.zerocode.hotelPackagesApi.exception.NotFoundException;

public interface AdminService {
    void  createAdmin(CreateAdminRequest createAdminRequest)throws NotFoundException;
    void  updateAdmin(Long id, UpdateAdminRequest updateAdminRequest)throws NotFoundException;
    void  deleteAdmin(Long id)throws NotFoundException;
}
