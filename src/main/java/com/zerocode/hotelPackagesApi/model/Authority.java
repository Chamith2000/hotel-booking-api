package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.zerocode.hotelPackagesApi.model.User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private com.zerocode.hotelPackagesApi.model.Hotel hotel;
}
