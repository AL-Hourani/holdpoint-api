package com.alhourani.holdpoint.resource.dto;

import java.time.Instant;
import java.util.UUID;

public record ResourceResponse(
    UUID id,
    String name,
    String resourceKey,
    Long capacity,
    Long reservedQuantity,
    Long confirmedQuantity,
    Instant createAt
) {}
