package com.alhourani.holdpoint.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alhourani.holdpoint.resource.dto.CreateResourceRequest;
import com.alhourani.holdpoint.resource.dto.ResourceResponse;
import com.alhourani.holdpoint.resource.dto.UpdateResourceCapacityRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController 
@RequiredArgsConstructor 
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private  final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(
       @Valid  @RequestBody CreateResourceRequest  request) {
        //TODO: process POST request

        ResourceResponse resource = resourceService.createResource(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(
        @PathVariable UUID id) {

        return ResponseEntity.ok(
            resourceService.getResourceById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> listResources(){
        var resources =  resourceService.listResources();
        return ResponseEntity.ok(resources);
    }



    @GetMapping("/key/{resourceKey}")
    public ResponseEntity<ResourceResponse> getResourceByKey(
            @PathVariable String resourceKey) {

        return ResponseEntity.ok(
                resourceService.getResourceByKey(resourceKey)
        );
    }



    @PatchMapping("/{id}/capacity")
    public ResponseEntity<ResourceResponse> updateCapacity(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateResourceCapacityRequest request) {

        return ResponseEntity.ok(
                resourceService.updateCapacity(id, request)
        );
    }
    
    

}
