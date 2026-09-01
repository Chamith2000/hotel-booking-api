package com.zerocode.hotelPackagesApi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class SuperDealListItem {
    private LocalDate startDate;
    private LocalDate endDate;
    private double discountedPrice;
    private PackageListItem packageListItem;
}
