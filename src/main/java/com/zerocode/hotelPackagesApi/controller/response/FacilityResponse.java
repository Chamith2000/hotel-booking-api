package com.zerocode.hotelPackagesApi.controller.response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityResponse {
    private Long id;
    private String categoryName;
}
