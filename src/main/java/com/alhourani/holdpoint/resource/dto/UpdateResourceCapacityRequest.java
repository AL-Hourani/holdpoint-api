package com.alhourani.holdpoint.resource.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateResourceCapacityRequest(
    
    @NotNull 
    @Positive 
    Long capacity
) {

}
