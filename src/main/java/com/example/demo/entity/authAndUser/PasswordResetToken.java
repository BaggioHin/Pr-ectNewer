package com.example.demo.entity.authAndUser;

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
@Table(name = "password_reset_tokens",
        indexes = {
                @Index(name = "idx_password_reset_token_hash", columnList = "token_hash")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    @NotBlank
    @Size(max = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    @NotNull
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime usedAt;
}
