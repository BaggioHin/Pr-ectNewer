package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalerTransactionResponse {
    private String studentName;
    private String courseName;
    private Long amount;
    private LocalDateTime occurredAt;
}
