package com.alhourani.holdpoint.workspace.dto;

import java.time.Instant;
import java.util.UUID;

import com.alhourani.holdpoint.workspace.WorkSpaceStatus;

public record WorkspaceResponse(
    
    UUID id,

    String name,
    
    String slug,

    WorkSpaceStatus status,

    UUID createdBy,

    Instant createdAy


) {

}
