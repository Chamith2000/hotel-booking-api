package com.zerocode.hotelPackagesApi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zerocode.hotelPackagesApi.model.GuestCount;
import com.zerocode.hotelPackagesApi.model.PackageImage;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePackageRequestDTO {

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Start date is mandatory")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotNull(message = "End date is mandatory")
    @Future(message = "End date must be in the future")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @NotNull(message = "Guest count is mandatory")
    @JsonProperty("guest_count")
    private GuestCount guestCount;

    @Min(value = 1, message = "Visitors must be at least 1")
    @Max(value = 100, message = "Visitors cannot exceed 100")
    private int visitors;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;

    @NotBlank(message = "Terms and conditions are mandatory")
    @JsonProperty("terms_&_condition")
    private String termsAndCondition;

    @NotEmpty(message = "At least one image is required")
    private List<PackageImage> images;

    @NotNull(message = "Status is mandatory")
    private Boolean status;

    @AssertTrue(message = "End date must be after start date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate);
    }
}