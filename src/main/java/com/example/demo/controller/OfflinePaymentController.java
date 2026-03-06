package com.example.demo.controller;

import com.example.demo.dto.request.OfflinePaymentConfirmRequest;
import com.example.demo.dto.request.OfflinePaymentCreateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OfflinePaymentConfirmResponse;
import com.example.demo.dto.response.OfflinePaymentCreateResponse;
import com.example.demo.service.k1.OfflinePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/offline")
public class OfflinePaymentController {

    @Autowired
    private OfflinePaymentService offlinePaymentService;

    @PostMapping("/request")
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    ApiResponse<OfflinePaymentCreateResponse> createPayment(@RequestBody OfflinePaymentCreateRequest request) {
        applySalerContext(request);
        return ApiResponse.<OfflinePaymentCreateResponse>builder()
                .result(offlinePaymentService.createOfflinePayment(request))
                .build();
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    ApiResponse<OfflinePaymentConfirmResponse> confirmPayment(@RequestBody OfflinePaymentConfirmRequest request) {
        return ApiResponse.<OfflinePaymentConfirmResponse>builder()
                .result(offlinePaymentService.confirmOfflinePayment(request))
                .build();
    }

    private void applySalerContext(OfflinePaymentCreateRequest request) {
        if (request == null) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return;
        }
        boolean isSaler = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SALER".equals(a.getAuthority()));
        if (!isSaler) {
            return;
        }
        Long userId = jwt.getClaim("userId");
        if (userId != null) {
            request.setSalerUserId(userId);
        }
    }
}
