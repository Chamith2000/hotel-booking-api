package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class HotelMenuList {
    private Long id;
    private String  type;
    private List<MenuItem>menuItems;
}
