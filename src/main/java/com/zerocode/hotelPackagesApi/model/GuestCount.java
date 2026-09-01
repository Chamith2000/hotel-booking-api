package com.zerocode.hotelPackagesApi.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class GuestCount {
    private int adults;
    private int children;
}
