
package com.alhourani.holdpoint.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public final class SessionConfig {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SessionConfig() {
        // Prevent creating instances of this utility class.
    }

    public static String generateRefreshToken() {

        byte[] randomBytes = new byte[32];

        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public static String hashToken(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available", e
            );
        }
    }
}
