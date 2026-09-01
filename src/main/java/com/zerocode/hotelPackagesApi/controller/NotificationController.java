package com.zerocode.hotelPackagesApi.controller;

import com.zerocode.hotelPackagesApi.controller.request.CreateNotificationRequestDTO;
import com.zerocode.hotelPackagesApi.service.NotificationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @RolesAllowed({"ADMIN"})
    @PostMapping(value = "/notifications")
    public void createNotification(@Valid @RequestBody CreateNotificationRequestDTO createNotificationRequestDTO){
        notificationService.createNotification(createNotificationRequestDTO);
    }
}
