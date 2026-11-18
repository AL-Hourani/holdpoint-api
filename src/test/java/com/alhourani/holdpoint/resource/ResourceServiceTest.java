package com.alhourani.holdpoint.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alhourani.holdpoint.common.exception.InvalidCapacityException;
import com.alhourani.holdpoint.common.exception.ResourceAlreadyExistsException;
import com.alhourani.holdpoint.common.exception.ResourceNotFoundException;
import com.alhourani.holdpoint.resource.dto.CreateResourceRequest;
import com.alhourani.holdpoint.resource.dto.ResourceResponse;
import com.alhourani.holdpoint.resource.dto.UpdateResourceCapacityRequest;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {


    @Mock 
    private ResourceRepository resourceRepository;

    @InjectMocks 
    private ResourceService resourceService;

    private Resource resource;

    private UUID resourceId;


    @BeforeEach 

    void setUp(){
        resourceId = UUID.randomUUID();

        resource = new Resource();

        resource.setId(resourceId);
        resource.setResourceKey("concert-ticket");
        resource.setCapacity(100L);
        resource.setReservedQuantity(20L);
        resource.setConfirmedQuantity(30L);
        resource.setCreatedAt(Instant.now());
    }




    // CREATE RESOURCES
    @Test 
    void shouldCreateResourceSuccessfully(){

        CreateResourceRequest request = new CreateResourceRequest(
            "concert-ticket","tiket", 100L);

        when(resourceRepository.existsByResourceKey("concert-ticket"))
        .thenReturn(false);

        when(resourceRepository.save(any(Resource.class)))
        .thenAnswer(invocation -> {
             Resource saved = invocation.getArgument(0);
                saved.setId(resourceId);
                saved.setCreatedAt(resource.getCreatedAt());
                return saved;
        });


        ResourceResponse response =
            resourceService.createResource(request);

        assertNotNull(response);
        assertEquals(resourceId, response.id());
        assertEquals("concert-ticket", response.resourceKey());
        assertEquals(100L, response.capacity());
        assertEquals(0L, response.reservedQuantity());
        assertEquals(0L, response.confirmedQuantity());

        verify(resourceRepository).save(any(Resource.class));
        
    }


    // RESOURCE KEY LOWER CASE
    
    @Test 
    void shouldNormalizeResourceKeyToLowercase(){
        CreateResourceRequest request =
            new CreateResourceRequest(" Concert-Ticket ", "ticket",  100L);


             when(resourceRepository.existsByResourceKey("concert-ticket"))
            .thenReturn(false);

            when(resourceRepository.save(any(Resource.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

              ResourceResponse response =
            resourceService.createResource(request);

        assertEquals("concert-ticket", response.resourceKey());

        verify(resourceRepository)
            .existsByResourceKey("concert-ticket");

    }//end normalize resource key



    @Test
    void shouldThrowExceptionWhenResourceKeyAlreadyExists() {

        CreateResourceRequest request =
            new CreateResourceRequest("concert-ticket","ticket" ,  100L);

        when(resourceRepository.existsByResourceKey("concert-ticket"))
            .thenReturn(true);

        assertThrows(
            ResourceAlreadyExistsException.class,
            () -> resourceService.createResource(request)
        );

        verify(resourceRepository, never())
            .save(any(Resource.class));
    }

    // GET RESOURCE BY ID


     @Test
    void shouldReturnResourceWhenIdExists() {

        when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.of(resource));

        ResourceResponse response =
            resourceService.getResourceById(resourceId);

        assertNotNull(response);
        assertEquals(resourceId, response.id());
        assertEquals("concert-ticket", response.resourceKey());

        verify(resourceRepository).findById(resourceId);
    }



     @Test
    void shouldThrowExceptionWhenResourceIdDoesNotExist() {

        when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> resourceService.getResourceById(resourceId)
        );
    }

    // GET RESOURCE BY KEY


    @Test
    void shouldThrowExceptionWhenResourceKeyDoesNotExist() {

        when(resourceRepository.findByResourceKey("unknown-resource"))
            .thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> resourceService.getResourceByKey("unknown-resource")
        );
    }





     // UPDATE CAPACITY

    @Test
    void shouldUpdateCapacitySuccessfully() {

        when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.of(resource));

        UpdateResourceCapacityRequest request =
            new UpdateResourceCapacityRequest(120L);

        ResourceResponse response =
            resourceService.updateCapacity(resourceId, request);

        assertEquals(120L, response.capacity());
        assertEquals(20L, response.reservedQuantity());
        assertEquals(30L, response.confirmedQuantity());
    }

    @Test
    void shouldRejectCapacityBelowAllocatedQuantity() {

        when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.of(resource));

        UpdateResourceCapacityRequest request =
            new UpdateResourceCapacityRequest(40L);

        assertThrows(
            InvalidCapacityException.class,
            () -> resourceService.updateCapacity(resourceId, request)
        );

        assertEquals(100L, resource.getCapacity());

        verify(resourceRepository, never())
            .save(any(Resource.class));
    }


}
