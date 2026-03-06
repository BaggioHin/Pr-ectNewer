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
public class OfflinePaymentConfirmResponse {
    private boolean success;
    private String txnRef;
    private String confirmationCode;
    private PaymentStatus status;
}
