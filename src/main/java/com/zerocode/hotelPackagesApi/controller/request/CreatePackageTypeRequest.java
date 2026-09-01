package com.zerocode.hotelPackagesApi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CreatePackageTypeRequest {
    @NotBlank
    private String name;
}
