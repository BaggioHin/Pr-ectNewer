package com.example.demo.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResponse {
    private boolean valid;
}