package com.example.demo.entity.sales;

import com.example.demo.entity.authAndUser.User;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Saler {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull
    private User user;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(max = 50)
    private String code;

    @Column(name = "team", length = 100)
    @Size(max = 100)
    private String team;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
