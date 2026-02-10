package com.example.demo.dto.response;

import com.example.demo.constant.Language;
import com.example.demo.constant.Theme;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private LocalDate dob;
    private String email;
    private String phone;
    private String avatar;
    private String bio;
    private LocalDateTime lastLogin;
    private Theme theme;
    private Language language;
}
