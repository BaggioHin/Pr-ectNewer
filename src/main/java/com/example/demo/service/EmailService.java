package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendResetPasswordEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("""
                We received a request to reset your password.
                Click the link below to set a new password:
                %s
                
                If you did not request this, you can ignore this email.
                """.formatted(resetLink));
        mailSender.send(message);
    }

    public void sendEnrollmentSuccessEmail(String to, String className, String courseName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Payment successful - Enrollment confirmed");
        message.setText("""
                Your payment was successful.
                Class: %s
                Course: %s
                
                Thank you.
                """.formatted(
                className != null ? className : "UNKNOWN",
                courseName != null ? courseName : "UNKNOWN"
        ));
        mailSender.send(message);
    }

    public void sendSalerPaymentSuccessEmail(String to, String studentName, String className, String courseName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Payment successful - Student enrolled");
        message.setText("""
                A student has completed payment.
                Student: %s
                Class: %s
                Course: %s
                
                Thank you.
                """.formatted(
                studentName != null ? studentName : "UNKNOWN",
                className != null ? className : "UNKNOWN",
                courseName != null ? courseName : "UNKNOWN"
        ));
        mailSender.send(message);
    }
}
