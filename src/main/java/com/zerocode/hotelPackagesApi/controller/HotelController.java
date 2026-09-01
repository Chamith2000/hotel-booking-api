package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.response.HotelListItem;
import com.zerocode.hotelPackagesApi.controller.response.HotelListResponse;
import com.zerocode.hotelPackagesApi.controller.response.HotelResponse;
import com.zerocode.hotelPackagesApi.exception.HotelApprovalException;
import com.zerocode.hotelPackagesApi.exception.HotelNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.service.HotelService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class HotelController {

    private HotelService hotelService;

    @RolesAllowed({"CUSTOMER"})
    @PostMapping(value = "/hotels",headers = "X-Api-Version=v1")
    public void createHotel(@RequestBody CreateHotelRequest createHotelRequest) throws HotelNotCreatedException {
        hotelService.createHotel(createHotelRequest);
    }

    @RolesAllowed({"CUSTOMER", "ADMIN"})
    @GetMapping(value = "/hotels",headers = "X-Api-Version=v1")
    public HotelListResponse getAllHotels() {
        List<Hotel> hotelList = hotelService.findAll();
        var hotels = hotelList.stream()
                .map(hotel -> HotelListItem.builder()
                        .id(hotel.getId())
                        .name(hotel.getName())
                        .address(hotel.getAddress())
                        .logo(hotel.getLogo())
                        .hotelPhotos(hotel.getHotelPhotos())
                        .status(hotel.getStatus())
                        .email(hotel.getEmail())
                        .boostPackageLimit(hotel.getBoostPackageLimit())
                        .contactNumbers(hotel.getContactNumbers())
                        .superDealLimit(hotel.getSuperDealLimit())
                        .build())
                .toList();

        return HotelListResponse.builder()
                .hotelList(hotels)
                .build();
    }

    @RolesAllowed({"HOTEL", "ADMIN", "CUSTOMER"})
    @GetMapping(value = "/hotels/{hotel-id}",headers = "X-Api-Version=v1")
    public HotelResponse getHotelById(@PathVariable ("hotel-id") Long hotelId) throws HotelNotFoundException {
        Hotel hotel = hotelService.findById(hotelId);
        var hotelItem = HotelListItem.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .logo(hotel.getLogo())
                .hotelPhotos(hotel.getHotelPhotos())
                .status(hotel.getStatus())
                .email(hotel.getEmail())
                .boostPackageLimit(hotel.getBoostPackageLimit())
                .contactNumbers(hotel.getContactNumbers())
                .superDealLimit(hotel.getSuperDealLimit())
                .build();

        return HotelResponse.builder()
                .hotelListItem(hotelItem)
                .build();
    }

    @RolesAllowed({"HOTEL"})
    @PatchMapping(value = "/hotels/{hotel-id}",headers = "X-Api-Version=v1")
    public void updateHotel(@PathVariable ("hotel-id")Long hotelId , @RequestBody UpdateHotelRequest updateHotelRequest) throws HotelNotFoundException {
        hotelService.updateHotel(hotelId,updateHotelRequest);
    }

    @RolesAllowed({"HOTEL"})
    @DeleteMapping(value = "/hotels/{hotel-id}",headers = "X-Api-Version=v1")
    public void deleteHotel(@PathVariable ("hotel-id") Long hotelId) throws HotelNotFoundException {
        hotelService.deleteHotel(hotelId);
    }

    @RolesAllowed({"HOTEL", "ADMIN"})
    @GetMapping(value = "/hotels/filterByStatus", headers = "X-Api-Version=v1")
    public HotelListResponse findByStatus(@RequestParam(value = "status") Boolean status) throws HotelNotFoundException {
      List <Hotel> hotelList = hotelService.findByStatus(status);
        var hotels = hotelList.stream()
                .map(hotel -> HotelListItem.builder()
                        .id(hotel.getId())
                        .name(hotel.getName())
                        .address(hotel.getAddress())
                        .logo(hotel.getLogo())
                        .hotelPhotos(hotel.getHotelPhotos())
                        .status(hotel.getStatus())
                        .email(hotel.getEmail())
                        .boostPackageLimit(hotel.getBoostPackageLimit())
                        .contactNumbers(hotel.getContactNumbers())
                        .superDealLimit(hotel.getSuperDealLimit())
                        .build())
                .toList();

        return HotelListResponse.builder()
                .hotelList(hotels)
                .build();


    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/pending")
    public List<Hotel> getPendingHotels() {
        return hotelService.getPendingHotels();
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/{hotelId}/approve")
    public ResponseEntity<String> approveHotel(@PathVariable Long hotelId) {
        try {
            hotelService.approveHotel(hotelId);
            return ResponseEntity.ok("Hotel Approved Successfully");
        } catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Hotel not found with ID: " + hotelId);
        } catch (HotelApprovalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/{hotelId}/reject")
    public ResponseEntity<String> rejectHotel(@PathVariable Long hotelId) {
        try {
            hotelService.rejectHotel(hotelId);
            return ResponseEntity.ok("Hotel Rejected Successfully");
        } catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Hotel not found with ID: " + hotelId);
        } catch (HotelApprovalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
