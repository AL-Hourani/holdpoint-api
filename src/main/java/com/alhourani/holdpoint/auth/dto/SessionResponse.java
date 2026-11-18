package com.alhourani.holdpoint.auth.dto;

import java.time.Instant;
import java.util.UUID;

import com.alhourani.holdpoint.auth.session.SessionStatus;

public record SessionResponse(

        UUID id,

        SessionStatus status,

        Instant createdAt,

        Instant lastUsedAt,

        Instant expiresAt,

        Instant revokedAt
) {

}
