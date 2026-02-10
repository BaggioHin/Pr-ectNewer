package com.example.demo.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectRequest {
    private String token;
}