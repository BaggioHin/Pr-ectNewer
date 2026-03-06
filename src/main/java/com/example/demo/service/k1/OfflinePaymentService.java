package com.example.demo.service.k1;

import com.example.demo.dto.request.OfflinePaymentConfirmRequest;
import com.example.demo.dto.request.OfflinePaymentCreateRequest;
import com.example.demo.dto.response.OfflinePaymentConfirmResponse;
import com.example.demo.dto.response.OfflinePaymentCreateResponse;

public interface OfflinePaymentService {
    OfflinePaymentCreateResponse createOfflinePayment(OfflinePaymentCreateRequest request);

    OfflinePaymentConfirmResponse confirmOfflinePayment(OfflinePaymentConfirmRequest request);
}
