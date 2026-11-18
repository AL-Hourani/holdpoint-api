package com.alhourani.holdpoint.reservation.dto;

import java.time.Instant;
import java.util.UUID;

import com.alhourani.holdpoint.reservation.ReservationStatus;

public record ReservationResponse(

    UUID id,
    String resourceKey,
    Long quantity,
    ReservationStatus status,
    Instant expiresAt,
    Instant createdAt
) {}
