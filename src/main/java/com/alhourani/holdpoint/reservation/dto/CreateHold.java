package com.alhourani.holdpoint.reservation.dto;

public record CreateHold(
    String resourceKey,
    Long quantity
) {}
