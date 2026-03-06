package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VnpayReturnResponse {
    private boolean validSignature;
    private String responseCode;
    private String transactionStatus;
    private String txnRef;
    private Long amount;
    private String orderInfo;
    private String bankCode;
    private String payDate;
}
