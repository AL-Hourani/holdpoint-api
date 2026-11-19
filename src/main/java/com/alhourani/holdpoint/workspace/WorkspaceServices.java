package com.alhourani.holdpoint.workspace;


import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alhourani.holdpoint.workspace.dto.CreateWorkspaceRequest;
import com.alhourani.holdpoint.workspace.dto.WorkspaceResponse;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional 
public class WorkspaceServices {

    private final WorkspaceRepository workspaceRepository;

    // CREATE WORKSPACE
    public WorkspaceResponse create(CreateWorkspaceRequest request ,UUID created_by){

        Workspace workspace = Workspace.create(
            request.name(),
            request.slug(),
            created_by
        );

        Workspace newWorkspace = workspaceRepository.save(workspace);

        return to_Response(newWorkspace);
    }




    // GET WORKSPACE BY ID
    public WorkspaceResponse getWorkspaceById(UUID workspaceId){

        Workspace workspace = workspaceRepository.findById(workspaceId)
        .orElseThrow(
            ()-> new IllegalArgumentException(
                    "workspace not found with ID: " + workspaceId
            )
        );

        return to_Response(workspace);
    }




    // UTILE METHOD
    private WorkspaceResponse to_Response(Workspace workspace){
        return new   WorkspaceResponse(
            workspace.getId(),
            workspace.getName(),
            workspace.getSlug(),
            workspace.getStatus(),
            workspace.getCreatedBy(),
            workspace.getCreatedAt()
        );
    }
    
}
