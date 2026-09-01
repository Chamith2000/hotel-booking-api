package com.zerocode.hotelPackagesApi.service;

import com.zerocode.hotelPackagesApi.controller.request.CreateNotificationRequestDTO;

public interface NotificationService {
    public void createNotification(CreateNotificationRequestDTO createNotificationRequestDTO);
}
