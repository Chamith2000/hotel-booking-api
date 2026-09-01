package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateAdminRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateAdminRequest;
import com.zerocode.hotelPackagesApi.exception.NotFoundException;
import com.zerocode.hotelPackagesApi.model.Admin;
import com.zerocode.hotelPackagesApi.repository.AdminRepository;
import com.zerocode.hotelPackagesApi.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private AdminRepository adminRepository;
    @Override
    public void createAdmin(CreateAdminRequest createAdminRequest) throws NotFoundException {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(createAdminRequest.getEmail());
        if(optionalAdmin.isPresent()){
            throw new NotFoundException("Admin already exists");
        }
        Admin admin = new Admin();
        admin.setEmail(createAdminRequest.getEmail());
        admin.setPassword(createAdminRequest.getPassword());
        admin.setProfilePhotoUrl(createAdminRequest.getProfilePhotoUrl());
        admin.setUserName(createAdminRequest.getUserName());
        adminRepository.save(admin);
    }

    @Override
    public void updateAdmin(Long id, UpdateAdminRequest updateAdminRequest) throws NotFoundException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found this "+id));
        admin.setEmail(updateAdminRequest.getEmail());
        admin.setProfilePhotoUrl(updateAdminRequest.getProfilePhotoUrl());
        admin.setUserName(updateAdminRequest.getUserName());
        adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(Long id) throws NotFoundException {
        Admin admin =  adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found this "+id));
        adminRepository.delete(admin);
    }
}
