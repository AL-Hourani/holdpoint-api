package com.alhourani.holdpoint.resource;

import java.util.UUID;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alhourani.holdpoint.common.exception.InvalidCapacityException;
import com.alhourani.holdpoint.common.exception.LowResourceCapacity;
import com.alhourani.holdpoint.common.exception.ResourceAlreadyExistsException;
import com.alhourani.holdpoint.common.exception.ResourceNotFoundException;
import com.alhourani.holdpoint.resource.dto.CreateResourceRequest;
import com.alhourani.holdpoint.resource.dto.ResourceResponse;
import com.alhourani.holdpoint.resource.dto.UpdateResourceCapacityRequest;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional 
public class ResourceService {
    
    private final ResourceRepository resourceRepository;

    // CREATE RESOURCE
    public ResourceResponse createResource(CreateResourceRequest request){

        String resourceKey = normalizeResourceKey(request.resourceKey());

        if(resourceRepository.existsByResourceKey(resourceKey)){
            throw new ResourceAlreadyExistsException(
                "Resource key already exists: "+ resourceKey
            );
        }


        Resource resource = new Resource();

        resource.setResourceKey(resourceKey);
        resource.setCapacity(request.capacity());
        resource.setName(request.name());
        resource.setReservedQuantity(0L);
        resource.setConfirmedQuantity(0L);


        Resource savedResource = resourceRepository.save(resource);

        return new ResourceResponse(
                savedResource.getId(),
                savedResource.getName(),
                savedResource.getResourceKey(),
                savedResource.getCapacity(),
                savedResource.getReservedQuantity(),
                savedResource.getConfirmedQuantity(),
                savedResource.getCreatedAt()
            );

    } // create resource end


    // GET RESOURCE BY ID

    @Transactional(readOnly = true)
    public  ResourceResponse getResourceById(UUID resourceId){
        // check resource if exist
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with ID: " + resourceId
            ));

        return toResponse(resource);
    }



    // GET RESOURCE BY RESOURCE KEY
    @Transactional(readOnly = true)
    public ResourceResponse getResourceByKey(String resourceKey) {

        String normalizedKey = normalizeResourceKey(resourceKey);

        Resource resource = resourceRepository.findByResourceKey(normalizedKey)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with key: " + normalizedKey
            ));

        return toResponse(resource);
    }



    // UPDATE CAPACITY 
    public ResourceResponse updateCapacity(
        UUID resourceId,
        UpdateResourceCapacityRequest request) {

        
        if(resourceId == null || request == null){
            throw new IllegalArgumentException(
                "resource id or request should not be null"
            );
        }


        Resource resource = resourceRepository.findByIdForUpdate(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with ID: " + resourceId
            ));

        if (resource.getCapacity() <= 0){
            throw new LowResourceCapacity(
                "capacity should not be smaller than zero"
            );
        }

        Long newCapacity = request.capacity();

        Long reserved = resource.getReservedQuantity();
        Long confirmed = resource.getConfirmedQuantity();

        if (confirmed > newCapacity ||
            reserved > newCapacity - confirmed) {

            throw new InvalidCapacityException(
                    "New capacity cannot be lower than allocated quantity"
                );
              
        }

        resource.setCapacity(newCapacity);

    
        return toResponse(resource);
    }






    // LIST RESOURCES
    public List<ResourceResponse> listResources(){
        List<Resource> resources = resourceRepository.findAll();
         
        return resources.stream().map(
            this::toResponse
        ).toList();
    }



    // UTILS METHODS
    private ResourceResponse toResponse(Resource resource){
        return new ResourceResponse(
            resource.getId(),
            resource.getName(),
            resource.getResourceKey(),
            resource.getCapacity(),
            resource.getReservedQuantity(),
            resource.getConfirmedQuantity(),
            resource.getCreatedAt()
        );
    }


    private String normalizeResourceKey(String resourceKey) {
        return resourceKey.trim().toLowerCase(Locale.ROOT);
    }


  
} // class end
