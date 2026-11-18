package com.alhourani.holdpoint.auth.dto;

import java.util.UUID;

public record RotatedSession(
        UUID userId,
        String fullName,
        String email,
        String refreshToken
) {

}
