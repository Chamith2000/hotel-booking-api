package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "facility_categories")
public class FacilityCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @ManyToMany(mappedBy = "facilityCategories",cascade = CascadeType.ALL)
    private List<HotelPackage> hotelPackages;

    @OneToMany(mappedBy = "facilityCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityItem> facilityItems;
}
