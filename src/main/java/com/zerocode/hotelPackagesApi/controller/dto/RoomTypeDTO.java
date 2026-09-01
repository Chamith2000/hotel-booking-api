package com.zerocode.hotelPackagesApi.controller.dto;

import com.zerocode.hotelPackagesApi.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeDTO {
    private RoomType roomType;
    private double price;


}
