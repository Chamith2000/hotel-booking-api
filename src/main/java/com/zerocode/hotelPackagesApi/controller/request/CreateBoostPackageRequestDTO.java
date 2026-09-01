package com.zerocode.hotelPackagesApi.controller.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBoostPackageRequestDTO {
    private LocalDate boostedDate;
}
