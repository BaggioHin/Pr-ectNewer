package com.example.demo.entity.notificationAndLog;

import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_type", columnList = "type"),
                @Index(name = "idx_notification_ref", columnList = "ref_type,ref_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 2000)
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", nullable = false, length = 30)
    @NotNull
    private NotificationRefType refType;

    @Column(name = "ref_id", nullable = false)
    @NotNull
    private Long refId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
