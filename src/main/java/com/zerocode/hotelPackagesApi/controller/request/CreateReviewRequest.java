package com.zerocode.hotelPackagesApi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {

    @Min(1)
    private int rating;
    @NotBlank
    private String comment;
    @NotNull
    private LocalDateTime postedDateAndTime;
}
