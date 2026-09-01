package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository <Notification, Long> {

//    List<Notification> findByStatus(String unread);
}
