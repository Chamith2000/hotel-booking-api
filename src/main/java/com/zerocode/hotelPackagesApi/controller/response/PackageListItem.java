package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.HotelPackage;
import com.zerocode.hotelPackagesApi.model.PackageImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageListItem {
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

//    public PackageListItem(HotelPackage hotelPackage) {
//    }

    public PackageListItem(HotelPackage hotelPackage) {
        this.id = hotelPackage.getId();
        this.name = hotelPackage.getName();
        this.price = hotelPackage.getPrice();
        this.description = hotelPackage.getDescription();
        this.startDate = hotelPackage.getStartDate();
        this.endDate = hotelPackage.getEndDate();
        this.termsAndCondition = hotelPackage.getTermsAndCondition();
        this.visitorCount = hotelPackage.getVisitorCount();
        this.status = hotelPackage.getStatus();
        this.guestAdults = (hotelPackage.getGuestCount() != null) ? hotelPackage.getGuestCount().getAdults() : 0;
        this.guestChildren = (hotelPackage.getGuestCount() != null) ? hotelPackage.getGuestCount().getChildren() : 0;
        this.imageUrls = hotelPackage.getPackageImages(); // Image list eka map karanawa
    }
}