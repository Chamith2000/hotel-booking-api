package com.zerocode.hotelPackagesApi.controller.response;

import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.model.Notification;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String userName;
    private String email;
    private String profilePhotoUrl;
    private ApprovalStatus approvalStatus;
    private List<Notification> notifications;
}
