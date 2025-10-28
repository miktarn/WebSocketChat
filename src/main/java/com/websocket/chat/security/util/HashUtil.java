package com.websocket.chat.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private static final String CRYPTO_ALGORITHM = "SHA-512";
    private static final String FORMAT_SPECIFIER = "%02x";

    public static String hashPassword(String password) {
        StringBuilder hashedPassword = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(CRYPTO_ALGORITHM);
            byte[] digest = messageDigest.digest(password.getBytes());
            for (byte b : digest) {
                hashedPassword.append(String.format(FORMAT_SPECIFIER, b));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't hash password", e);
        }
        return hashedPassword.toString();
    }
}
