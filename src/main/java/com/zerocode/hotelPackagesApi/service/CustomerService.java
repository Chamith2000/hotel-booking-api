package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.dto.CustomerDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateCustomerRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateCustomerRequest;
import com.zerocode.hotelPackagesApi.controller.response.CustomerList;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;


import java.util.List;

public interface CustomerService {
    CreateCustomerRequest addCustomer(CreateCustomerRequest createCustomerRequest) throws CustomerNotFoundException;
    void update(Long id, UpdateCustomerRequest updateCustomerRequest) throws CustomerNotFoundException;
    void delete(Long id) throws CustomerNotFoundException;
    CustomerDTO getById(Long id) throws CustomerNotFoundException;
    void addProfilePhoto(Long id, String profilePhotoUrl) throws CustomerNotFoundException;
    void updateUserName(Long id, String userName) throws CustomerNotFoundException;
    void updateUserEmail(Long id, String email) throws CustomerNotFoundException;
    List<CustomerList> findAll();
}
