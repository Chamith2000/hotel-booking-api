package com.zerocode.hotelPackagesApi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "hotel_packages")
@AllArgsConstructor
@NoArgsConstructor
public class HotelPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String termsAndCondition;
    private int visitorCount;
    private Boolean status;

    @Enumerated(EnumType.STRING)
    private PackageStatus packageStatus;

    @Embedded
    private GuestCount guestCount;

    @OneToOne(mappedBy = "hotelPackage")
    @JsonManagedReference
    private BoostPackage boostPackage;

    @OneToOne(mappedBy = "hotelPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private SuperDeal superDeals;

    @ManyToOne(fetch = FetchType.EAGER)
    private Hotel hotel;


    @ManyToOne
    private PackageType packageType;

    @OneToMany(mappedBy = "hotelPackage", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PackageImage> packageImages;

    @ManyToOne
    private RoomType roomType;

    @ManyToOne
    private HotelMenu hotelMenu;

    @ManyToMany
    @JoinTable(name = "hotel_package_facility_item",
            joinColumns = @JoinColumn(name = "hotel_package_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_item_id"))
    private List<FacilityItem> facilityItems;

    @ManyToMany
    @JoinTable(name = "hotel_package_facility_categories",
            joinColumns = @JoinColumn(name = "hotel_package_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<FacilityCategory> facilityCategories;

}