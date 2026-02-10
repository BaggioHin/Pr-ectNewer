package com.example.demo.repository;

import com.example.demo.entity.notificationAndLog.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
