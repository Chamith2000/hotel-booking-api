package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHotelRequest {
    private String password;
    private List<HotelImageDTO> hotelImageDTO;
    private List<HotelContactNumberDTO> hotelContactNumberDTO;
    private String boostPackageLimit;
    private String superDealLimit;
    private Boolean status;

}
