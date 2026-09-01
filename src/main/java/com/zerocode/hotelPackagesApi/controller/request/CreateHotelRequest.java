package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import com.zerocode.hotelPackagesApi.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHotelRequest {
    private String name;
    private String email;
    private String password;
    private Address address;
    private List<HotelContactNumberDTO> contactNumbers;
    private String logo;
    private List<HotelImageDTO> hotelPhotos;
    private String boostPackageLimit;
    private String superDealLimit;
    private Boolean status;



}
