package com.example.loginpage.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service

public class otpService {
    private final JavaMailSender mailSender;
    private static final Random random = new Random();
    private static final Map<String, String> otpStore = new HashMap<>();

    public otpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + random.nextInt(900000)); // 6-digit OTP
        otpStore.put(email, otp);
        sendOtpEmail(email, otp);
        return otp;
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Registration");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String inputOtp) {
        return otpStore.containsKey(email) && otpStore.get(email).equals(inputOtp);
    }
}
