package com.example.demo.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignIn {
    private String username;
    private String password;
}
