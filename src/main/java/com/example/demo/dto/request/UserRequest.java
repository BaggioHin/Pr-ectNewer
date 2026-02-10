package com.example.demo.dto.request;

import com.example.demo.constant.CreateType;
import com.example.demo.constant.Language;
import com.example.demo.constant.Theme;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String name;
    private LocalDate dob;
    private String email;
    private String phone;
    private String avatar;
    private String bio;
    private CreateType role;
    private Long CreatedBy;
    private Language language;
    private Theme theme;
}
