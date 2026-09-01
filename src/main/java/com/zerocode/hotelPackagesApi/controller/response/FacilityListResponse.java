package com.zerocode.hotelPackagesApi.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class FacilityListResponse {
    private List<FacilityList>  facilities;
}
