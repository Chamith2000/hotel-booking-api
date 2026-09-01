package com.zerocode.hotelPackagesApi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@AllArgsConstructor
@Data
@Builder
public class HotelListResponse {
    private List<HotelListItem> hotelList;
}
