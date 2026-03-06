package com.example.demo.service.impl;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.entity.authAndUser.PasswordResetToken;
import com.example.demo.entity.authAndUser.InvalidationTokenEntity;
import com.example.demo.entity.authAndUser.Role;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.InValidationTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.k1.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InValidationTokenRepository invalidationTokenRepository;

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.reset-password-url:http://localhost:5173/reset-password}")
    private String resetPasswordUrl;

    @Value("${app.reset-password-exp-minutes:30}")
    private long resetPasswordExpMinutes;


    @Override
    public AuthenticationResponse authentication(AuthenticationRequest authenticationRequest) {
        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(
                authenticationRequest.getPassword(),
                user.getPassword()
        );

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var accessToken = generateAccessToken(user);
        var refreshToken = generateRefreshToken(user);

        user.getProfile().setLastLogin(LocalDateTime.now());
        userRepository.save(user);


        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .success(true)
                .name(user.getUsername())
                .userId(user.getId())
                .roles(roles)
                .build();
    }

    public String generateAccessToken(User user) {
        JWSHeader header =new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet =new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Baggio")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("SCOPE", buildScope(user))
                .claim("userId", user.getId())
                .claim("token_type", "access")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            log.debug("Signer key bytes: {}", Arrays.toString(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException e){
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(User user) {
        JWSHeader header =new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet =new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Baggio")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("token_type", "refresh")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            log.debug("Signer key bytes: {}", Arrays.toString(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException e){
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        String token = introspectRequest.getToken();
        boolean isValid = true;
        try{
            verifyAccessToken(token);
        }catch (AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException{
        try{
            SignedJWT signedJWT = verifyAccessToken(logoutRequest.getToken());

            String Jid = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidationTokenEntity invalidationTokenEntity = InvalidationTokenEntity.builder()
                    .id(Jid).expiryTime(expirationDate).build();

            invalidationTokenRepository.save(invalidationTokenEntity);
        }catch (AppException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyRefreshToken(refreshToken);
        String Jid = signedJWT.getJWTClaimsSet().getJWTID();
        String username = signedJWT.getJWTClaimsSet().getSubject();
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        if(invalidationTokenRepository.existsById(Jid)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        invalidationTokenRepository.save(InvalidationTokenEntity.builder()
                .id(Jid).expiryTime(expirationDate).build());

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); 
        var accessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        var userOpt = userRepository.findByEmail(request.getEmail().trim());
        if (userOpt.isEmpty()) {
            return "If the email exists, a reset link has been sent.";
        }
        User user = userOpt.get();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return "If the email exists, a reset link has been sent.";
        }

        passwordResetTokenRepository.deleteByUser_Id(user.getId());

        String rawToken = UUID.randomUUID().toString().replace("-", "");
        String tokenHash = hashToken(rawToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(resetPasswordExpMinutes);

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        passwordResetTokenRepository.save(token);

        String link = resetPasswordUrl + "?token=" + rawToken;
        emailService.sendResetPasswordEmail(user.getEmail(), link);
        return "If the email exists, a reset link has been sent.";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        if (request == null || request.getToken() == null || request.getNewPassword() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (request.getToken().isBlank() || request.getNewPassword().isBlank()) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        String tokenHash = hashToken(request.getToken().trim());
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AppException(ErrorCode.RESET_TOKEN_INVALID));
        if (token.isUsed()) {
            throw new AppException(ErrorCode.RESET_TOKEN_INVALID);
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        token.setUsed(true);
        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
        return "Reset password successful!";
    }

//    @Override
//    public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) throws ParseException, JOSEException {
//        SignedJWT signedJWT = verifyToken(changePasswordRequest.getToken(),true);
//        String username = signedJWT.getJWTClaimsSet().getSubject();
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        boolean authenticated = passwordEncoder.matches(user.getPassword(), changePasswordRequest.getOldPassword());
//        if(!authenticated) {
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
//        return ChangePasswordResponse.builder().success(true).build();
//    }

    public SignedJWT verifyAccessToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String tokenType = signedJWT.getJWTClaimsSet().getStringClaim("token_type");

        var verified = signedJWT.verify(verifier);
        if(!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(!"access".equals(tokenType)) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(invalidationTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String tokenType = signedJWT.getJWTClaimsSet().getStringClaim("token_type");

        var verified = signedJWT.verify(verifier);
        if(!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(!"refresh".equals(tokenType)) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(invalidationTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public String buildScope(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));
    }

    private String hashToken(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
