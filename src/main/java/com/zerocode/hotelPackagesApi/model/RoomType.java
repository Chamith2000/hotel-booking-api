package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "room_types")
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private Double price;

    @OneToMany(mappedBy = "roomType" , cascade = CascadeType.ALL)
    private List<HotelPackage> hotelPackages;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL)
    private List<HotelRoomType> roomTypes;


}
