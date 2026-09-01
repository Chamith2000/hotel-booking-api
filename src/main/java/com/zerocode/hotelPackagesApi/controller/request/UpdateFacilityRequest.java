package com.zerocode.hotelPackagesApi.controller.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateFacilityRequest {
    private String name;
    private String category;
}
