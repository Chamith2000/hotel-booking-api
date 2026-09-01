package com.zerocode.hotelPackagesApi.controller.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LoyaltyPointList {
    private double count;
    private LocalDate earnedDate;
}
