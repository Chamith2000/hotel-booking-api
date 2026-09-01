package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.Address;
import com.zerocode.hotelPackagesApi.model.HotelContactNumber;
import com.zerocode.hotelPackagesApi.model.HotelImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class HotelListItem {
    private Long id;
    private String name;
    private Address address;
    private String logo;
    private List<HotelImage> hotelPhotos;
    private List<HotelContactNumber> contactNumbers;
    private String email;
    private String boostPackageLimit;
    private String superDealLimit;
    private Boolean status;
}
