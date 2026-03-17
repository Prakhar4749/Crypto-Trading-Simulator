package com.prakhar.auth.utils;

import java.util.Random;
import java.util.UUID;

public class OtpUtils {

    public static String generateOTP() {
        int otpLength = 6;
        Random random = new Random();
        StringBuilder otp = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public static String generateEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }
}
