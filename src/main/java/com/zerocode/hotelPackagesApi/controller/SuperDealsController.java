package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateSuperDealsRequest;
import com.zerocode.hotelPackagesApi.controller.response.*;
import com.zerocode.hotelPackagesApi.exception.NotCreatedException;
import com.zerocode.hotelPackagesApi.exception.NotFoundException;
import com.zerocode.hotelPackagesApi.exception.SuperDealNotFoundException;
import com.zerocode.hotelPackagesApi.exception.PackageNotFoundException;
import com.zerocode.hotelPackagesApi.model.SuperDeal;
import com.zerocode.hotelPackagesApi.service.SuperDealsService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class SuperDealsController {

    private SuperDealsService superDealsService;
    private PackageController packageController;

    @RolesAllowed({"HOTEL"})
    @PostMapping(value = "/packages/{package-id}/super-deals", headers = "X-Api-Version=v1")
    public void create(@PathVariable("package-id")Long packageID,@RequestBody CreateSuperDealsRequest createSuperDealsRequest) throws NotCreatedException {
        superDealsService.create(packageID,createSuperDealsRequest);
    }

    @RolesAllowed({"HOTEL", "ADMIN", "CUSTOMER"})
    @GetMapping(value = "/super-deals", headers = "X-Api-Version=v1")
    public SuperDealsListResponse getAllSuperDeals() {
        List<SuperDeal> superDealList = superDealsService.findAll();
        var superDeals = superDealList.stream()
                .map(superDeal -> SuperDealListItem.builder()
                        .startDate(superDeal.getStartDate())
                        .endDate(superDeal.getEndDate())
                        .discountedPrice(superDeal.getDiscountedPrice())
                        .packageListItem(PackageListItem.builder()
                                .id(superDeal.getId())
                                .name(superDeal.getHotelPackage().getName())
                                .price(superDeal.getHotelPackage().getPrice())
                                .description(superDeal.getHotelPackage().getDescription())
                                .startDate(superDeal.getStartDate())
                                .endDate(superDeal.getEndDate())
                                .termsAndCondition(superDeal.getHotelPackage().getTermsAndCondition())
                                .visitorCount(superDeal.getHotelPackage().getVisitorCount())
                                .status(superDeal.getHotelPackage().getStatus())
                                .guestAdults(superDeal.getHotelPackage().getGuestCount() != null ? superDeal.getHotelPackage().getGuestCount().getAdults() : 0)
                                .guestChildren(superDeal.getHotelPackage().getGuestCount() != null ? superDeal.getHotelPackage().getGuestCount().getChildren() : 0)
                                .imageUrls(superDeal.getHotelPackage().getPackageImages())
                                .build())
                        .build())
                .toList();

        return SuperDealsListResponse.builder()
                .superDeals(superDeals)
                .build();
    }

    @RolesAllowed({"HOTEL", "CUSTOMER"})
    @GetMapping(value = "/super-deals/{super-deal-id}",headers = "X-Api-Version=v1")
    public SuperDealResponse getSuperDealById(@PathVariable("super-deal-id") Long superDealId) throws SuperDealNotFoundException, PackageNotFoundException {
        SuperDeal superDeal = superDealsService.findById(superDealId);
        Long id = superDeal.getHotelPackage().getId();
        PackageListItem hotelPackage = packageController.getPackageById(id);
        var superDealItem = SuperDealListItem.builder()
                .startDate(superDeal.getStartDate())
                .endDate(superDeal.getEndDate())
                .discountedPrice(superDeal.getDiscountedPrice())
                .packageListItem(hotelPackage)
                .build();
        return SuperDealResponse.builder()
                .superDealListItem(superDealItem)
                .build();
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @DeleteMapping(value = "/super-deals/{super-deal-id}",headers = "X-Api-Version=v1")
     public void deleteById(@PathVariable("super-deal-id") Long superDealId) throws SuperDealNotFoundException {
        superDealsService.deleteById(superDealId);
    }

}
