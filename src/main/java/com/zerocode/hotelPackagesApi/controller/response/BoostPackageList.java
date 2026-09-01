package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.PackageImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoostPackageList {
    private Long id;
    private String name;
    private double price;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String termsAndCondition;
    private int visitorCount;
    private Boolean status;
    private int guestAdults;
    private int guestChildren;
    private List<PackageImage> imageUrls;
    private LocalDate boostedDate;
}
