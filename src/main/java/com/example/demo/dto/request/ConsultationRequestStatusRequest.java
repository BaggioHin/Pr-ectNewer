package com.example.demo.dto.request;

import com.example.demo.constant.ConsultationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequestStatusRequest {
    private ConsultationStatus status;
}
