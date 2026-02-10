package com.example.demo.entity.authAndUser;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InvalidationTokenEntity {
    @Id
    @NotBlank
    @Size(max = 255)
    String id;

    @NotNull
    Date expiryTime;
}
