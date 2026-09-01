package com.zerocode.hotelPackagesApi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String logo;
    private String boostPackageLimit;
    private String superDealLimit;
    private Boolean status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<HotelImage> hotelPhotos;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<HotelPackage> packages;

    @ManyToMany(mappedBy = "hotelList")
    private List<Notification> notificationList;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<HotelMenu> hotelMenuList;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<BoostPackage> boostPackages;

    @OneToMany(mappedBy = "hotel")
    private List<SuperDeal> superDeals;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<HotelContactNumber> contactNumbers;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<HotelRoomType> hotelRoomTypes;

    @ManyToMany
    @JoinTable(name = "hotel_package_type",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "package_type_id"))
    private List<PackageType> packageTypes;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<com.zerocode.hotelPackagesApi.model.Authority> authorities;
}