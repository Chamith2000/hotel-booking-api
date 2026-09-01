package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "super_deals")
public class SuperDeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double discountedPrice;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private HotelPackage hotelPackage;

    @ManyToOne
    private Hotel hotel;

}
