package com.example.demo.dto.response;

import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private Boolean success;
    private Long userId;
    private String name;
    private Set<String> roles;
}
