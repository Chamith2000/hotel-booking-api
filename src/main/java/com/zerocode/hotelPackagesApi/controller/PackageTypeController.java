package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeList;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeListResponse;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotFoundException;
import com.zerocode.hotelPackagesApi.service.PackageTypeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;



@RestController
@AllArgsConstructor
@Transactional
public class PackageTypeController {

    private PackageTypeService packageTypeService;

    @RolesAllowed({"ADMIN"})
    @PostMapping(value = "/package-types",headers = "X-Api-Version=v1")
    public void create(@RequestBody @Valid CreatePackageTypeRequest createPackageTypeRequest)throws PackageTypeNotCreatedException{
        packageTypeService.create(createPackageTypeRequest);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(value = "/package-types", headers = "X-Api-Version=v1")
    public PackageTypeListResponse getAll(){
       return packageTypeService.getAll();
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(value = "/package-types/{package-type-id}", headers = "X-Api-Version=v1")
    public PackageTypeList getById(@PathVariable("package-type-id")Long packageTypeId)throws PackageTypeNotFoundException{
        return packageTypeService.getById(packageTypeId);
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping(value = "/package-types/{package-type-id}", headers = "X-Api-Version=v1")
    public void delete(@PathVariable("package-type-id")Long packageTypeId ) throws PackageTypeNotFoundException{
        packageTypeService.delete(packageTypeId);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping(value = "/package-types/{package-type-id}",headers = "X-Api-Version=v1")
    public void update(@PathVariable("package-type-id")Long packageTypeId,@RequestBody UpdatePackageTypeRequest updatePackageTypeRequest)throws PackageTypeNotFoundException{
        packageTypeService.update(packageTypeId,updatePackageTypeRequest);
    }

}
