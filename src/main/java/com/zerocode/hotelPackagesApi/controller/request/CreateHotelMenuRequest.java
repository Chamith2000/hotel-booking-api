package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.model.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateHotelMenuRequest {

    @NotBlank
    private String type;
    @NotNull
    private List<MenuItem> menuItems;
}
