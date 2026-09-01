package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "hotel_menus")
public class HotelMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;

    @OneToMany(mappedBy = "hotelMenu", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MenuItem> menuItems;


    @OneToMany(mappedBy = "hotelMenu")
    private List<HotelPackage> hotelPackages;

    @ManyToOne
    private Hotel hotel;



}