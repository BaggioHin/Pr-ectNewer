package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VnpayCreateRequest {
    private Long amount;
    private String orderInfo;
    private String orderType;
    private Long courseClassId;
    private Long studentId;
    private Long salerUserId;
}
