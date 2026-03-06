package com.example.demo.service.k1;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authentication(AuthenticationRequest authenticationRequest);
    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;
    void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(String refreshToken) throws ParseException, JOSEException;
    String forgotPassword(ForgotPasswordRequest request);
    String resetPassword(ResetPasswordRequest request);
//    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) throws ParseException, JOSEException;
}
