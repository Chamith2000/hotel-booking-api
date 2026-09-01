package com.zerocode.hotelPackagesApi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageTypeListResponse {
    private List<PackageTypeList>packageTypeLists;
}
