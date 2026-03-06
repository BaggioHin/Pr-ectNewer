package com.example.demo.dto.request;

import com.example.demo.constant.Language;
import com.example.demo.constant.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String name;
    private LocalDate dob;
    private String email;
    private String phone;
    private String bio;
    private Language language;
    private Theme theme;
}
