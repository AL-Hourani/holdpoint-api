package com.alhourani.holdpoint.workspace;

import java.time.Instant;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
    name = "workspaces",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_workspaces_slug",
            columnNames = "slug"
        )
    }

)
@Getter 
public class Workspace {

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column (nullable = false  , length = 150)
    private String name;

    @Column(nullable =  false , length = 150)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkSpaceStatus status;

    @Column(name = "created_by" , nullable =  false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @PrePersist 
    protected  void onCreate(){
        this.createdAt = Instant.now();

    }


    public static Workspace create(
        String name,
        String slug,
        UUID createdBy
    ){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "Workspace name is required"
            );
        }

        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException(
                "Workspace slug is required"
            );
        }

        if (createdBy == null) {
            throw new IllegalArgumentException(
                "Creation information is required"
            );
        }


        Workspace workspace = new Workspace();
        workspace.name = name.trim();
        workspace.slug = slug;
        workspace.createdBy = createdBy;
        workspace.status = WorkSpaceStatus.ACTIVE;

        return  workspace;
    }




    // status

    public void transitionTo(WorkSpaceStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                "New status is required"
            );
        }

        if (this.status == newStatus) {
            return;
        }


        boolean allowed = switch(this.status){
            case PENDING -> 
                        newStatus == WorkSpaceStatus.ACTIVE ||
                        newStatus == WorkSpaceStatus.DELETED;

            case ACTIVE ->
                newStatus == WorkSpaceStatus.SUSPENDED ||
                newStatus == WorkSpaceStatus.ARCHIVED;

            case SUSPENDED ->
                newStatus == WorkSpaceStatus.ACTIVE ||
                newStatus == WorkSpaceStatus.ARCHIVED;

            case ARCHIVED ->
                newStatus == WorkSpaceStatus.ACTIVE ||
                newStatus == WorkSpaceStatus.DELETED;

            case DELETED -> false;
        };


        if (!allowed) {
            throw new IllegalStateException(
                "Invalid workspace status transition: "
                + status + " -> " + newStatus
            );
        }

        this.status = newStatus;

    }
}
