package com.zerocode.hotelPackagesApi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PackageListResponse {
    @JsonProperty("packages")  // This ensures the JSON field name is "packages"
    private List<PackageListItem> packages;  // Changed from "packageListItems" to "packages"
}
