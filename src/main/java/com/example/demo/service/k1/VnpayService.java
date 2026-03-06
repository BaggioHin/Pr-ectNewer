package com.example.demo.service.k1;

import com.example.demo.dto.request.VnpayCreateRequest;
import com.example.demo.dto.response.VnpayCreateResponse;
import com.example.demo.dto.response.VnpayReturnResponse;

import java.util.Map;

public interface VnpayService {
    VnpayCreateResponse createPaymentUrl(VnpayCreateRequest request, String clientIp);

    VnpayReturnResponse handleReturn(Map<String, String> params);

    Map<String, String> handleIpn(Map<String, String> params);
}
