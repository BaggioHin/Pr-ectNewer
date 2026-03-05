package com.example.demo.controller;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.service.k1.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authentication(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response) {
        AuthenticationResponse authenticationResponse = authenticationService.authentication(authenticationRequest);
        setRefreshCookie(authenticationResponse.getRefreshToken(), response);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationResponse)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refreshToken")
    ApiResponse<AuthenticationResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse)
            throws ParseException, JOSEException {
        String refreshToken = resolveRefreshToken(request, httpRequest);
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
        setRefreshCookie(response.getRefreshToken(), httpResponse);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }

    private String resolveRefreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        if (httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return request != null ? request.getToken() : null;
    }

    private void setRefreshCookie(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            return;
        }
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

//    @PostMapping("/changePassword")
//    ApiResponse<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request)
//            throws ParseException, JOSEException {
//        return ApiResponse.<ChangePasswordResponse>builder()
//                .result(authenticationService.changePassword(request))
//                .build();
//    }
}
