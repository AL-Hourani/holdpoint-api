
package com.alhourani.holdpoint.auth.dto;

import java.time.Instant;
import java.util.UUID;

import com.alhourani.holdpoint.user.UserStatus;

public record RegisterResponse(

        UUID id,

        String fullName,

        String email,

        UserStatus status,

        Instant createdAt

) {
}
