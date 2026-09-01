package com.zerocode.hotelPackagesApi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "boost_packages")
public class BoostPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate boostedDate;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "package_id")
    @JsonBackReference
    private HotelPackage hotelPackage;

    @ManyToOne
    private Hotel hotel;
}