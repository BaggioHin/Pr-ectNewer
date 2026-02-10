package com.example.demo.entity.sales;

import com.example.demo.constant.ConsultationSource;
import com.example.demo.constant.ConsultationStatus;
import com.example.demo.constant.ConsultationTypeReceive;
import com.example.demo.entity.authAndUser.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultation_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_receive", nullable = false, length = 20)
    @NotNull
    private ConsultationTypeReceive typeReceive;

    @Column(name = "phone", nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    @NotNull
    private String phone;

    @Column(name = "email", length = 255)
    @Email
    @Size(max = 255)
    private String email;

    @Column(name = "message", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    @NotNull
    private ConsultationSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private ConsultationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_saler_id")
    private Saler assignedSaler;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
