package com.zerocode.hotelPackagesApi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class HotelResponse {
    private HotelListItem hotelListItem;
}
