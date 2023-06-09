package com.capstone.project.utils;

import java.security.SecureRandom;

public class PasswordResetCodeGenerator {
    private static final int CODE_LENGTH = 5;
    private static final String CODE_CHARS = "0123456789";

    public static String generateResetCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CODE_CHARS.length());
            char randomChar = CODE_CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
