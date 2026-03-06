package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflinePaymentCreateRequest {
    private Long amount;
    private String orderInfo;
    private Long courseClassId;
    private Long studentId;
    private Long salerUserId;
    private String paymentMethod;
    private String paymentInstructions;
}
