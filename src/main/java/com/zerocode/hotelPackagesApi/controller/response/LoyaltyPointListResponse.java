package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoyaltyPointListResponse {
    List<LoyaltyPoint> loyaltyPointList;
}
