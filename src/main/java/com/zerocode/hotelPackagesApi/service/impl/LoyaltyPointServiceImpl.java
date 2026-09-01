package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateLoyaltyPointRequest;
import com.zerocode.hotelPackagesApi.controller.response.LoyaltyPointList;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.LoyaltyPointNotFoundException;
import com.zerocode.hotelPackagesApi.model.Customer;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import com.zerocode.hotelPackagesApi.repository.CustomerRepository;
import com.zerocode.hotelPackagesApi.repository.LoyaltyPointRepository;
import com.zerocode.hotelPackagesApi.service.LoyaltyPointService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LoyaltyPointServiceImpl implements LoyaltyPointService {
    private LoyaltyPointRepository loyaltyPointRepository;
    private CustomerRepository customerRepository;

    @Override
    public void addLoyaltyPoint(Long customerId, CreateLoyaltyPointRequest createLoyaltyPointRequest) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " not found"));

        LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
        loyaltyPoint.setCount(createLoyaltyPointRequest.getCount());
        loyaltyPoint.setEarnedDate(createLoyaltyPointRequest.getEarnedDate());
        loyaltyPointRepository.save(loyaltyPoint);
        
        // Update customer with loyalty point
        customer.setLoyaltyPoint(loyaltyPoint);
        customerRepository.save(customer);
    }

    @Override
    public void updateLoyaltyPoint(Long customerId, LoyaltyPoint loyaltyPoint) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " not found"));
        
        if (customer.getLoyaltyPoint() == null) {
            throw new CustomerNotFoundException("Customer with id " + customerId + " has no loyalty points");
        }
        
        LoyaltyPoint existingLoyaltyPoint = customer.getLoyaltyPoint();
        existingLoyaltyPoint.setEarnedDate(loyaltyPoint.getEarnedDate());
        existingLoyaltyPoint.setCount(loyaltyPoint.getCount());
        loyaltyPointRepository.save(existingLoyaltyPoint);
    }

    @Override
    public LoyaltyPoint getLoyaltyPointById(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " not found"));
        
        if (customer.getLoyaltyPoint() == null) {
            throw new CustomerNotFoundException("Customer with id " + customerId + " has no loyalty points");
        }
        
        return customer.getLoyaltyPoint();
    }

}
