package com.zerocode.hotelPackagesApi.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminListResponse {
    private List<AdminList> adminList;
}
