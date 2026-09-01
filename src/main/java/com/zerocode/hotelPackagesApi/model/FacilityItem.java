package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "facility_items")
public class FacilityItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    private Boolean isActive;

    @ManyToOne
    private FacilityCategory facilityCategory;

    @ManyToMany(mappedBy = "facilityItems", cascade = CascadeType.ALL)
    private List<HotelPackage> hotelPackages;
}
