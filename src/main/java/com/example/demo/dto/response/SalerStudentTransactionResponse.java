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
public class SalerStudentTransactionResponse {
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String courseName;
    private String courseClassName;
    private LocalDateTime occurredAt;
}
