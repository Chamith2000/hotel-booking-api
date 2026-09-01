package com.zerocode.hotelPackagesApi.controller.response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BoostPackageListResponse {
    private List<BoostPackageList> boostPackageLists;
}
