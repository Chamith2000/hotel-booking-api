package com.zerocode.hotelPackagesApi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateSuperDealsRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private double discountedPrice;
}
