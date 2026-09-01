package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.CreateNotificationRequestDTO;
import com.zerocode.hotelPackagesApi.model.Notification;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.NotificationRepository;
import com.zerocode.hotelPackagesApi.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private HotelRepository hotelRepository;
    private NotificationRepository notificationRepository;

    @Override
    public void createNotification(CreateNotificationRequestDTO createNotificationRequestDTO) {
        Notification notification = new Notification();
        notification.setMessage(createNotificationRequestDTO.getMessage());
        notification.setSentDateAndTime(createNotificationRequestDTO.getSentDateAndTime());
        notification.setStatus(createNotificationRequestDTO.isStatus());
        notification.setRole(createNotificationRequestDTO.getRole());

        notificationRepository.save(notification);
    }
}
