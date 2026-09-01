//package com.zerocode.hotelPackagesApi.service.impl;
//
//import com.zerocode.hotelPackagesApi.controller.request.CreatePackageTypeRequest;
//import com.zerocode.hotelPackagesApi.controller.request.UpdatePackageTypeRequest;
//import com.zerocode.hotelPackagesApi.controller.response.PackageTypeList;
//import com.zerocode.hotelPackagesApi.controller.response.PackageTypeListResponse;
//import com.zerocode.hotelPackagesApi.exception.PackageTypeNotFoundException;
//import com.zerocode.hotelPackagesApi.model.PackageType;
//import com.zerocode.hotelPackagesApi.repository.PackageTypeRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
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
//class
//PackageTypeServiceImpISpringUnitTest {
//    @InjectMocks
//    private PackageTypeServiceImpl packageTypeService;
//
//    @Mock
//    private PackageTypeRepository packageTypeRepository;
//
//
//    @Test
//    @DisplayName("try to create member with already name")
//    public void testCreatePackageTypeWithDuplicateName() throws Exception {
//
//        String name = "Couple outing";
//
//        var packageType = new PackageType();
//        packageType.setId(1L);
//        packageType.setName(name);
//
//        Mockito.when(packageTypeRepository.findByName(name)).thenReturn(Optional.of(packageType));
//
//        var createNewPackageTypeDto = new CreatePackageTypeRequest();
//        createNewPackageTypeDto.setName(name);
//
//
//        assertThrows(Exception.class, () -> packageTypeService.create(createNewPackageTypeDto));
//
//    }
//
//    @Test
//    public void testCreatePackageType() throws Exception {
//
//        String name = "Couple outing";
//
//        var packageType = new PackageType();
//        packageType.setId(1L);
//        packageType.setName(name);
//
//        Mockito.when(packageTypeRepository.findByName(name)).thenReturn(Optional.empty());
//
//        Mockito.when(packageTypeRepository.save(Mockito.any(PackageType.class))).thenReturn(packageType);
//
//        var createRequest = new CreatePackageTypeRequest();
//        createRequest.setName(name);
//
//        packageTypeService.create(createRequest);
//        Mockito.verify(packageTypeRepository, Mockito.times(1)).save(Mockito.any(PackageType.class));
//
////        assertEquals(packageType.getName(), createPackageType.getName());
////        assertEquals(packageType.getId(),createPackageType.getId);
//    }
//
//    @Test
//    public void testGetAllPackageTypes() {
//
//        PackageType packageType = new PackageType();
//        packageType.setId(1L);
//        packageType.setName("uhi");
//
//        PackageType packageType1 = new PackageType();
//        packageType1.setId(2L);
//        packageType1.setName("jnhfdjvi");
//
//        List<PackageType> packageTypeList = Arrays.asList(packageType, packageType1);
//        Mockito.when(packageTypeRepository.findAll()).thenReturn(packageTypeList);
//
//        PackageTypeListResponse packageTypeListResponse = packageTypeService.getAll();
//
//        assertNotNull(packageTypeListResponse);
//        assertEquals(2, packageTypeListResponse.getPackageTypeLists().size());
//        assertEquals("uhi", packageTypeListResponse.getPackageTypeLists().get(0).getName());
//        assertEquals(1L,packageTypeListResponse.getPackageTypeLists().get(0).getId());
//        assertEquals("jnhfdjvi", packageTypeListResponse.getPackageTypeLists().get(1).getName());
//        assertEquals(2L,packageTypeListResponse.getPackageTypeLists().get(1).getId());
//
//        Mockito.verify(packageTypeRepository, times(1)).findAll();
//
//    }
//
//
//    @Test
//    public void testGetAllReturnEmptyList() {
//
//        when(packageTypeRepository.findAll()).thenReturn(Collections.emptyList());
//        PackageTypeListResponse packageTypeListResponse = packageTypeService.getAll();
//
//        assertNotNull(packageTypeListResponse);
//        assertTrue(packageTypeListResponse.getPackageTypeLists().isEmpty());
//
//        verify(packageTypeRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void testGetById() throws PackageTypeNotFoundException {
//
//        Long packageTypeId = 1L;
//        PackageType packageType = new PackageType();
//        packageType.setId(packageTypeId);
//        packageType.setName("jnhfdjvi");
//
//        when(packageTypeRepository.findById(packageTypeId)).thenReturn(Optional.of(packageType));
//
//        PackageTypeList packageTypeList = packageTypeService.getById(packageTypeId);
//
//        assertNotNull(packageTypeList);
//        assertEquals(packageTypeId, packageTypeList.getId());
//        assertEquals("jnhfdjvi", packageTypeList.getName());
//
//        verify(packageTypeRepository, times(1)).findById(packageTypeId);
//
//    }
//
//    @Test
//    public void testGetByIdPackageTypeNotFound() {
//
//        Long packageTypeId = 2L;
//
//        when(packageTypeRepository.findById(packageTypeId)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(PackageTypeNotFoundException.class, () -> {
//            packageTypeService.getById(packageTypeId);
//        });
//        assertEquals("Package type not found with Id" + packageTypeId, exception.getMessage());
//        verify(packageTypeRepository, times(1)).findById(packageTypeId);
//
//    }
//
//    @Test
//    public void testDeleteSuccess() throws PackageTypeNotFoundException {
//
//        Long packageTypeId = 1L;
//        PackageType packageType = new PackageType();
//        packageType.setId(packageTypeId);
//        packageType.setName("jnhfdjvi");
//
//        when(packageTypeRepository.findById(packageTypeId)).thenReturn(Optional.of(packageType));
//
//        packageTypeService.delete(packageTypeId);
//
//        verify(packageTypeRepository, times(1)).findById(packageTypeId);
//        verify(packageTypeRepository, times(1)).delete(packageType);
//    }
//
//    @Test
//    public void testDeletePackageNotFound() {
//
//        Long packageTypeId = 99L;
//        when(packageTypeRepository.findById(packageTypeId)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(PackageTypeNotFoundException.class, () -> {
//            packageTypeService.delete(packageTypeId);
//        });
//
//        assertEquals("Package type not found with Id" + packageTypeId, exception.getMessage());
//
//        verify(packageTypeRepository, times(1)).findById(packageTypeId);
//        verify(packageTypeRepository, never()).delete(any(PackageType.class));
//    }
//
//    @Test
//    public void testUpdatePackageType()throws PackageTypeNotFoundException{
//
//        Long packageTypeId = 1L;
//        var updatePackageTypeRequest = new UpdatePackageTypeRequest();
//        updatePackageTypeRequest.setName("name");
//
//        PackageType packageType = new PackageType();
//        packageType.setId(packageTypeId);
//        packageType.setName("namee");
//
//        when(packageTypeRepository.findById(packageTypeId)).thenReturn(Optional.of(packageType));
//        when(packageTypeRepository.save(any(PackageType.class))).thenReturn(packageType);
//
//        packageTypeService.update(packageTypeId,updatePackageTypeRequest);
//
//        assertEquals("name", packageType.getName());
//
//        verify(packageTypeRepository, times(1)).findById(packageTypeId);
//        verify(packageTypeRepository, times(1)).save(packageType);
//
//
//    }
//
//    @Test
//    public void testUpdatePackageTypeNotFound(){
//        Long packageTypeId = 99L;
//        UpdatePackageTypeRequest updatePackageTypeRequest = new UpdatePackageTypeRequest();
//        updatePackageTypeRequest.setName("name");
//
//        when(packageTypeRepository.findById(packageTypeId)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(PackageTypeNotFoundException.class, ()-> {
//            packageTypeService.update(packageTypeId, updatePackageTypeRequest);
//        });
//        assertEquals("Package type not found with Id" + packageTypeId , exception.getMessage());
//
//        verify(packageTypeRepository, times(1)).findById(packageTypeId);
//        verify(packageTypeRepository, never()).save(any(PackageType.class));
//        }
//
//
//    }
//
