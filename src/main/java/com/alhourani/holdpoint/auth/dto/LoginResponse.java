
package com.alhourani.holdpoint.auth.dto;

import java.util.UUID;

public record LoginResponse(

        UUID userId,

        String fullName,

        String email,

        String accessToken,

        String refreshToken,

        String tokenType,

        long expiresIn

) {
}
