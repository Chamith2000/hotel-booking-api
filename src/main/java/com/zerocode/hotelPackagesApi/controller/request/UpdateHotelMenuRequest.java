package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.model.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHotelMenuRequest {
    private String type;
    private List<MenuItem>menuItems;

}
