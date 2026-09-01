package com.zerocode.hotelPackagesApi.controller.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateLoyaltyPointRequest {
    private Long id;
    private double count;
    private LocalDate earnedDate;
}
