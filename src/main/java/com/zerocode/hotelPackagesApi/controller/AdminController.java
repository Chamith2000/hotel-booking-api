package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateAdminRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateAdminRequest;
import com.zerocode.hotelPackagesApi.exception.NotFoundException;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.service.impl.AdminServiceImpl;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class AdminController {
    private final AdminServiceImpl adminServiceImpl;

    @RolesAllowed({"ADMIN"})
    @PostMapping(value = "/admins",headers = "X-Api-Version=v1")
    public void createAdmin(@RequestBody CreateAdminRequest createAdminRequest) throws NotFoundException {
        adminServiceImpl.createAdmin(createAdminRequest);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping(value = "/{admin-id}",headers = "X-Api-Version=v1")
    public void updateAdmin(@PathVariable(value = "admin-id") Long id, @RequestBody UpdateAdminRequest updateAdminRequest) throws NotFoundException {
        adminServiceImpl.updateAdmin(id, updateAdminRequest);
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping(value = "/{admin-id}",headers = "X-Api-Version=v1")
    public void deleteAdmin(@PathVariable(value = "admin-id") Long id) throws NotFoundException {
        adminServiceImpl.deleteAdmin(id);
    }

}
