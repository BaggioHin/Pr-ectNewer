package com.example.demo.entity.authAndUser;

import com.example.demo.constant.Language;
import com.example.demo.constant.Theme;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private Long id;

    @Size(max = 2048)
    private String avatar;

    @Size(max = 1000)
    private String bio;

    private LocalDateTime lastLogin;
    @Size(max = 100)
    private String name;

    @Past
    private LocalDate dob;

    @Size(max = 20)
    private String gender;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    @Size(max = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    private Language language;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
