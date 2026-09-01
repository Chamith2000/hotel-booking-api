//package com.zerocode.hotelPackagesApi.service.impl;
//
//import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
//import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
//import com.zerocode.hotelPackagesApi.controller.response.PackageTypeList;
//import com.zerocode.hotelPackagesApi.controller.response.PackageTypeListResponse;
//import com.zerocode.hotelPackagesApi.exception.PackageTypeNotCreatedException;
//import com.zerocode.hotelPackagesApi.exception.PackageTypeNotFoundException;
//import com.zerocode.hotelPackagesApi.model.PackageType;
//import com.zerocode.hotelPackagesApi.repository.PackageTypeRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class PackageTypeServiceImplIntergrationTest{
//
//    @Autowired
//    private PackageTypeServiceImpl packageTypeService;
//
//    @Autowired
//    private PackageTypeRepository packageTypeRepository;
//
//    @BeforeEach
//    public void clearDB(){
//        packageTypeRepository.deleteAll();
//
//    }
//
//    @Test
//    public void testCreatePackageType()throws Exception{
//        String name = "Couple outing";
//
//
//        CreatePackageTypeRequest createPackageTypeRequest = new CreatePackageTypeRequest();
//        createPackageTypeRequest.setName(name);
//
//        packageTypeService.create(createPackageTypeRequest);
//
//        List<PackageType> packageTypes = packageTypeRepository.findAll();
//        assertEquals(1,packageTypes.size());
//        assertEquals(name,packageTypes.get(0).getName());
//
//
//
//    }
//
//    @Test
//    public void testCreatePackageTypeWithDuplicateName()throws Exception {
//        String name = "Couple outing";
//
//        PackageType packageType = new PackageType();
//        packageType.setName(name);
//        packageTypeRepository.save(packageType);
//
//        CreatePackageTypeRequest createPackageTypeRequest = new CreatePackageTypeRequest();
//        createPackageTypeRequest.setName(name);
//
//        assertThrows(PackageTypeNotCreatedException.class, () -> packageTypeService.create(createPackageTypeRequest));
//    }
//
//    @Test
//    public void testGetAllPackageTypes()throws Exception{
//
//        PackageType packageType1 = new PackageType();
//        packageType1.setName("aaaa");
//
//        PackageType packageType2 = new PackageType();
//        packageType2.setName("bbb");
//
//        packageTypeRepository.saveAll(Arrays.asList(packageType1,packageType2));
//
//        PackageTypeListResponse packageTypeListResponse = packageTypeService.getAll();
//
//        assertNotNull(packageTypeListResponse);
//        assertEquals(2, packageTypeListResponse.getPackageTypeLists().size());
//
//    }
//
//    @Test
//    public void testGetAllPackageTypesReturnEmptyList()throws Exception{
//
//        List<PackageType> packageTypes = packageTypeRepository.findAll();
//
//        assertNotNull(packageTypes);
//        assertTrue(packageTypes.isEmpty());
//
//
//    }
//
//    @Test
//    public void testGetById()throws Exception{
//
//
//        PackageType packageType = new PackageType();
//
//        packageType.setName("aa");
//        packageType = packageTypeRepository.save(packageType);
//
//
//        PackageTypeList packageTypeList = packageTypeService.getById(packageType.getId());
//
//        assertNotNull(packageTypeList);
//        assertEquals(packageType.getId(),packageTypeList.getId());
//
//    }
//
//    @Test
//    public void testGetByIdNotFound()throws Exception{
//          assertThrows(PackageTypeNotFoundException.class,()-> packageTypeService.getById(99L));
//
//
//    }
//
//    @Test
//    public void testDeleteSuccess()throws Exception{
//        PackageType packageType = new PackageType();
//        packageType.setName("aa");
//        PackageType savedPackageType = packageTypeRepository.save(packageType);
//
//        assertDoesNotThrow(() -> packageTypeService.delete(savedPackageType.getId()));
//
//        assertFalse(packageTypeRepository.findById(savedPackageType.getId()).isPresent());
//
//}
//    @Test
//    public void testDeletePackageNotFound()throws Exception{
//
//         assertThrows(PackageTypeNotFoundException.class,()-> packageTypeService.delete(99L));
//
//
//    }
//
//    @Test
//    public void testUpdatePackageType()throws Exception{
//
//        PackageType packageType = new PackageType();
//        packageType.setName("aa");
//        packageType = packageTypeRepository.save(packageType);
//
//        UpdatePackageTypeRequest updatePackageTypeRequest = new UpdatePackageTypeRequest();
//        updatePackageTypeRequest.setName("Updated package");
//
//        packageTypeService.update(packageType.getId(),updatePackageTypeRequest);
//
//        PackageType updatePackageType = packageTypeRepository.findById(packageType.getId()).orElseThrow();
//        assertEquals("Updated package",updatePackageType.getName());
//
//    }
//
//    @Test
//    public void testUpdatePackageTypeNotFound()throws Exception{
//
//        UpdatePackageTypeRequest updatePackageTypeRequest = new UpdatePackageTypeRequest();
//        updatePackageTypeRequest.setName("aa");
//
//       assertThrows(PackageTypeNotFoundException.class,()-> packageTypeService.update(99L,updatePackageTypeRequest));
//
//
//    }
//
//}
