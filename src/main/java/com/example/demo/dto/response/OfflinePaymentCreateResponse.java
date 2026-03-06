package com.example.demo.dto.response;

import com.example.demo.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflinePaymentCreateResponse {
    private String txnRef;
    private Long amount;
    private String orderInfo;
    private String paymentMethod;
    private String paymentInstructions;
    private String transferCode;
    private PaymentStatus status;
}
