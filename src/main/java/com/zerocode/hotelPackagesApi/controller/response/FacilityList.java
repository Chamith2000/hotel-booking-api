package com.zerocode.hotelPackagesApi.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityList {
    private Long id;
    private String category;
}
