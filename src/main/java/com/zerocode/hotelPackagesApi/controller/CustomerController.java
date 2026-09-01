package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.dto.CustomerDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateCustomerRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateCustomerRequest;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.service.CustomerService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @RolesAllowed({"CUSTOMER"})
    @PostMapping(value = "/customers",headers = "X-Api-Version=v1")
    public void addCustomer(@RequestBody CreateCustomerRequest createCustomerRequest)throws CustomerNotFoundException {
        customerService.addCustomer(createCustomerRequest);
    }

    @RolesAllowed({"CUSTOMER", "ADMIN"})
    @GetMapping(value = "/customers/{customer_id}",headers = "X-Api-Version=v1")
    public CustomerDTO getCustomerById(@PathVariable Long customer_id) throws CustomerNotFoundException {
        return customerService.getById(customer_id);
    }

    @RolesAllowed({"CUSTOMER"})
    @PutMapping(value = "/customers/{customer_id}",headers = "X-Api-Version=v1")
    public void updateCustomerById(@PathVariable Long customer_id,@RequestBody UpdateCustomerRequest updateCustomerRequest) throws CustomerNotFoundException {
        customerService.update(customer_id,updateCustomerRequest);
    }

    @RolesAllowed({"CUSTOMER"})
    @DeleteMapping(value = "/customers/{customer_id}",headers = "X-Api-Version=v1")
    public void deleteCustomerById(@PathVariable Long customer_id) throws CustomerNotFoundException {
        customerService.delete(customer_id);
    }

    @RolesAllowed({"CUSTOMER"})
    @PutMapping(value = "/customers/{customer_id}/add-profile-picture",headers = "X-Api-Version=v1")
    public void addProfilePictureById(@PathVariable Long customer_id, @RequestBody String profilePicture) throws CustomerNotFoundException {
        customerService.addProfilePhoto(customer_id,profilePicture);
    }

    @RolesAllowed({"CUSTOMER"})
    @PutMapping(value = "/customers/{customer_id}/add-username",headers = "X-Api-Version=v1")
    public void updateUserName(@PathVariable Long customer_id, @RequestBody String username) throws CustomerNotFoundException {
        customerService.updateUserName(customer_id,username);
    }

    @RolesAllowed({"CUSTOMER"})
    @PutMapping(value = "/customers/{customerId}/add-email",headers = "X-Api-Version=v1")
    public void updateEmail(@PathVariable Long customerId, @RequestBody String email) throws CustomerNotFoundException {
        customerService.updateUserEmail(customerId,email);
    }


}
