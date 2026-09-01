package com.zerocode.hotelPackagesApi.controller.dto;

import com.zerocode.hotelPackagesApi.model.ApprovalStatus;
import com.zerocode.hotelPackagesApi.model.Notification;
import lombok.Data;

import java.util.List;

@Data
public class AdminDTO {
    private String userName;
    private String email;
    private String profilePhotoUrl;
    private ApprovalStatus approvalStatus;
    private List<Notification>  notifications;
}
