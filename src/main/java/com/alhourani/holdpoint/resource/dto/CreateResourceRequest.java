package com.alhourani.holdpoint.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public record CreateResourceRequest(
    @NotBlank 
    @Size(max = 150)
    String name,

    @NotBlank 
    @Size(max = 150)
    String resourceKey,

    @NotNull
    @Positive
    Long capacity
) {}
