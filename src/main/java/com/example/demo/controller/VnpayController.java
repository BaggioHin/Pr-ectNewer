package com.example.demo.controller;

import com.example.demo.dto.request.VnpayCreateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.VnpayCreateResponse;
import com.example.demo.dto.response.VnpayReturnResponse;
import com.example.demo.service.k1.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments/vnpay")
public class VnpayController {

    @Autowired
    private VnpayService vnpayService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    ApiResponse<VnpayCreateResponse> createPayment(
            @RequestBody VnpayCreateRequest request,
            HttpServletRequest httpRequest) {
        applySalerContext(request);
        String ipAddr = resolveClientIp(httpRequest);
        return ApiResponse.<VnpayCreateResponse>builder()
                .result(vnpayService.createPaymentUrl(request, ipAddr))
                .build();
    }

    @GetMapping("/return")
    ApiResponse<VnpayReturnResponse> handleReturn(@RequestParam Map<String, String> params) {
        return ApiResponse.<VnpayReturnResponse>builder()
                .result(vnpayService.handleReturn(params))
                .build();
    }

    @GetMapping("/ipn")
    Map<String, String> handleIpn(@RequestParam Map<String, String> params) {
        return vnpayService.handleIpn(params);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void applySalerContext(VnpayCreateRequest request) {
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
