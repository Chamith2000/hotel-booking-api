package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.dto.CustomerDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateCustomerRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateCustomerRequest;
import com.zerocode.hotelPackagesApi.controller.response.CustomerList;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.UserAlreadyExistsException;
import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
import com.zerocode.hotelPackagesApi.repository.LoyaltyPointRepository;
import com.zerocode.hotelPackagesApi.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private LoyaltyPointRepository loyaltyPointRepository;

    @Override
    public CreateCustomerRequest addCustomer(CreateCustomerRequest createCustomerRequest) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(createCustomerRequest.getEmail());
        if(optionalCustomer.isPresent()){
            throw new UserAlreadyExistsException("Customer with email " + createCustomerRequest.getEmail() + " already exists");
        }
        Customer newCustomer = new Customer();
        newCustomer.setEmail(createCustomerRequest.getEmail());
        newCustomer.setPassword(createCustomerRequest.getPassword());
        newCustomer.setUserName(createCustomerRequest.getUserName());
        newCustomer.setProfilePhotoUrl(createCustomerRequest.getProfilePhotoUrl());
        LoyaltyPoint  loyaltyPoint = new LoyaltyPoint();
        loyaltyPoint.setCount(5);
        loyaltyPointRepository.save(loyaltyPoint);
        newCustomer.setLoyaltyPoint(loyaltyPoint);

        customerRepository.save(newCustomer);
        return createCustomerRequest;
    }

    @Override
    public void update(Long id, UpdateCustomerRequest updateCustomerRequest) throws CustomerNotFoundException {
        Customer customer1 = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
        customer1.setEmail(updateCustomerRequest.getEmail());
        customer1.setPassword(updateCustomerRequest.getPassword());
        customer1.setProfilePhotoUrl(updateCustomerRequest.getProfilePhotoUrl());
        customer1.setUserName(updateCustomerRequest.getUserName());
        customerRepository.save(customer1);
    }

    @Override
    public void delete(Long id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
        customerRepository.delete(customer);
    }

    @Override
    public CustomerDTO getById(Long id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setUserName(customer.getUserName());
        customerDTO.setProfilePhotoUrl(customer.getProfilePhotoUrl());
        customerDTO.setLoyaltyPoint(customer.getLoyaltyPoint());
        return customerDTO;
    }


    @Override
    public void addProfilePhoto(Long id, String profilePhotoUrl) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
        customer.setProfilePhotoUrl(profilePhotoUrl);
        customerRepository.save(customer);
    }

    @Override
    public void updateUserName(Long id, String userName) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
        customer.setUserName(userName);
        customerRepository.save(customer);
    }

    @Override
    public void updateUserEmail(Long id, String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
        customer.setEmail(email);
        customerRepository.save(customer);
    }

@Override
public List<CustomerList> findAll(){
    List<Customer> customers = customerRepository.findAll();

    List<CustomerList> customerLists = customers.stream()
            .map(customer -> CustomerList.builder()
                    .id(customer.getId())
                    .userName(customer.getUserName())
                    .email(customer.getEmail())
                    .profilePhotoUrl(customer.getProfilePhotoUrl())
                    .loyaltyPoint(customer.getLoyaltyPoint())
                    .build())
            .collect(Collectors.toList());
    return customerLists;
}

}
