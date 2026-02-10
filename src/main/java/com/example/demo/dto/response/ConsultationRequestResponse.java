package com.example.demo.dto.response;

import com.example.demo.constant.ConsultationSource;
import com.example.demo.constant.ConsultationStatus;
import com.example.demo.constant.ConsultationTypeReceive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequestResponse {
    private Long id;
    private Long userId;
    private String phone;
    private String email;
    private String message;
    private ConsultationSource source;
    private ConsultationStatus status;
    private ConsultationTypeReceive typeReceive;
    private Long assignedSalerId;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
