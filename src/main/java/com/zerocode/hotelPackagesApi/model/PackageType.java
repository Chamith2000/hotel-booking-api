package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "package_types")
public class PackageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "packageType", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<HotelPackage> packages;

    @ManyToMany(mappedBy = "packageTypes", cascade = CascadeType.ALL)
    private List<Hotel> hotels;
}