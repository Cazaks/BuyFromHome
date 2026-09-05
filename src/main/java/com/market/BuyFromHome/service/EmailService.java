package com.market.BuyFromHome.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your BuyFromHome Stores password");
        message.setText(
                "We received a request to reset your password.\n\n" +
                        "Click the link below to set a new password:\n" + resetLink +
                        "\n\nThis link expires in 30 minutes. If you didn't request this, you can ignore this email."
        );
        mailSender.send(message);
    }
}