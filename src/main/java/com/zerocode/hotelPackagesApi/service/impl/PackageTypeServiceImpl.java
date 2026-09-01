package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeList;
import com.zerocode.hotelPackagesApi.controller.response.PackageTypeListResponse;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.PackageTypeNotFoundException;
import com.zerocode.hotelPackagesApi.model.PackageType;
import com.zerocode.hotelPackagesApi.repository.PackageTypeRepository;
import com.zerocode.hotelPackagesApi.service.PackageTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PackageTypeServiceImpl implements PackageTypeService {

    private PackageTypeRepository packageTypeRepository;

    @Override
    public void create(CreatePackageTypeRequest createPackageTypeRequest)throws PackageTypeNotCreatedException{
        Optional<PackageType>packageTypeOptional = packageTypeRepository.findByName(createPackageTypeRequest.getName());
        if (packageTypeOptional.isPresent()){
            throw new PackageTypeNotCreatedException("PackageType is already registered with name" + createPackageTypeRequest.getName());
        }
        PackageType packageType = new PackageType();
        packageType.setName((createPackageTypeRequest.getName()));

        packageTypeRepository.save(packageType);

    }

    @Override
    public PackageTypeListResponse getAll(){
        List<PackageTypeList> packageTypeLists = packageTypeRepository.findAll()
                .stream()
                .map(packageType -> PackageTypeList.builder()
                        .id(packageType.getId())
                        .name(packageType.getName())
                        .build())
                .toList();
        return new PackageTypeListResponse(packageTypeLists);

    }

    @Override

    public PackageTypeList getById(Long packageTypeId) throws PackageTypeNotFoundException {
        PackageType packageType = packageTypeRepository.findById(packageTypeId)
                .orElseThrow(()->new PackageTypeNotFoundException("Package type not found with Id"+ packageTypeId));

        return PackageTypeList.builder()
                .id(packageType.getId())
                .name(packageType.getName())
                .build();

    }

    @Override
    public void delete(Long packageTypeId)throws PackageTypeNotFoundException {
        PackageType packageType = packageTypeRepository.findById(packageTypeId)
                .orElseThrow(()-> new PackageTypeNotFoundException("Package type not found with Id" + packageTypeId));
        packageTypeRepository.delete(packageType);

    }

    @Override
    public void update(Long packageTypeId, UpdatePackageTypeRequest updatePackageTypeRequest)throws PackageTypeNotFoundException{
        PackageType packageType = packageTypeRepository.findById(packageTypeId)
                .orElseThrow(()-> new PackageTypeNotFoundException("Package type not found with Id" + packageTypeId));
        packageType.setName(updatePackageTypeRequest.getName());
        packageTypeRepository.save(packageType);


    }
}


