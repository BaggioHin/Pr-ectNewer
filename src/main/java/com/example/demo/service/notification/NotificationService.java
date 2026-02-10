package com.example.demo.service.notification;

import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.notificationAndLog.Notification;
import com.example.demo.entity.notificationAndLog.UserNotification;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserNotificationRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void notifyUsers(
            String title,
            String content,
            NotificationType type,
            NotificationRefType refType,
            Long refId,
            List<Long> userIds
    ) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .refType(refType)
                .refId(refId)
                .build();
        notificationRepository.save(notification);

        List<User> users = userRepository.findAllById(userIds);
        List<UserNotification> userNotifications = new ArrayList<>();
        for (User user : users) {
            userNotifications.add(UserNotification.builder()
                    .user(user)
                    .notification(notification)
                    .build());
        }
        userNotificationRepository.saveAll(userNotifications);
    }
}
