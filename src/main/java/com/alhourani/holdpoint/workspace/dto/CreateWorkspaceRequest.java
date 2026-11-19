package com.alhourani.holdpoint.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(

    @NotBlank
    @Size(max = 150) 
    String name,

    @NotBlank
    @Size(max = 150) 
    String slug
) {

}
