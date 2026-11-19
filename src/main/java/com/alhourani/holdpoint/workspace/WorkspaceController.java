package com.alhourani.holdpoint.workspace;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alhourani.holdpoint.workspace.dto.CreateWorkspaceRequest;
import com.alhourani.holdpoint.workspace.dto.WorkspaceResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController 
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor 
public class WorkspaceController {

    private final WorkspaceServices workspaceServices;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
        @Valid  @RequestBody CreateWorkspaceRequest request ,
        @AuthenticationPrincipal Jwt jwt
    ) {
        //TODO: process POST request

        UUID createdBy = UUID.fromString(jwt.getSubject());
        WorkspaceResponse response =
            workspaceServices.create(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
    }

    


    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(
        @PathVariable  UUID id) {
        
        WorkspaceResponse workspace = workspaceServices.getWorkspaceById(id);

        

        return ResponseEntity.ok(workspace);
    }
    
    
}
