package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String no;
    private String street;
    private String city;
    private String province;
    
}
