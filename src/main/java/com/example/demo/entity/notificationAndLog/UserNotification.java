package com.example.demo.entity.notificationAndLog;

import com.example.demo.entity.authAndUser.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_notification",
        indexes = {
                @Index(name = "idx_user_notification_user", columnList = "user_id"),
                @Index(name = "idx_user_notification_notification", columnList = "notification_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    @NotNull
    private Notification notification;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
