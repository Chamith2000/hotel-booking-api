package com.zerocode.hotelPackagesApi.controller.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateLoyaltyPointRequest {
    private double count;
    private LocalDate earnedDate;
}
