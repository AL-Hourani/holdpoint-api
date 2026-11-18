package com.alhourani.holdpoint.reservation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.alhourani.holdpoint.common.exception.ExpireReservationException;
import com.alhourani.holdpoint.common.exception.InvalidQuantityHold;
import com.alhourani.holdpoint.common.exception.LowResourceCapacity;
import com.alhourani.holdpoint.common.exception.ReservationNotFoundException;
import com.alhourani.holdpoint.common.exception.ResourceNotFoundException;
import com.alhourani.holdpoint.reservation.dto.CreateHold;
import com.alhourani.holdpoint.reservation.dto.ReservationResponse;
import com.alhourani.holdpoint.resource.Resource;
import com.alhourani.holdpoint.resource.ResourceRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;



    public ReservationResponse createHold(CreateHold request) {
    // Your implementation



        if (request == null) {
                throw new IllegalArgumentException(
                    "CreateHold request cannot be null"
                );
            }

        if(request.quantity() == null || request.quantity() <= 0){
            throw new InvalidQuantityHold(
                 "Reservation quantity must be greater than zero"
            );
        }


          // 2. Normalize resource key
    if (request.resourceKey() == null ||
            request.resourceKey().isBlank()) {
        throw new IllegalArgumentException(
            "Resource key cannot be blank"
        );
    }

    String resourceKey = request.resourceKey()
        .trim()
        .toLowerCase(java.util.Locale.ROOT);




        Resource resource = resourceRepository
            .findByResourceKeyForUpdate(resourceKey)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with key: " + resourceKey
            ));



        Long availableQuantity =
            resource.getCapacity()
            - resource.getReservedQuantity()
            - resource.getConfirmedQuantity();

        if (availableQuantity < request.quantity()) {
            throw new LowResourceCapacity(
                "Insufficient resource capacity"
            );
        }

        resource.setReservedQuantity(
            resource.getReservedQuantity() + request.quantity()
        );

        Reservation reservation = Reservation.createHold(
            resource , request.quantity()
        );

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return to_Response(savedReservation);

    }



    @Transactional
    public ReservationResponse confirmReservation(UUID reservationId){

        //GET RESERVATION
        Reservation reservation = reservationRepository.findByIdForUpdate(  
            reservationId
        ).orElseThrow(
            ()-> new ReservationNotFoundException(
                "Reservation not found with id: " + reservationId
            )
        );

        // GET LOCKER RESOURCE
        Resource resource = resourceRepository
            .findByIdForUpdate(reservation.getResource().getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with key: " + reservation.getResource().getId()
            ));

        boolean isExpire = reservation.getExpiresAt().isAfter(
            Instant.now());

        if(!isExpire){
            throw new ExpireReservationException(
                "Reservation is expired"
            );
        }
        
        if (reservation.getQuantity() > resource.getReservedQuantity()){
            throw new IllegalArgumentException(
                "Requested quantity can not be grater than reserved quantity"
            );
        }

        Reservation confirmedReservation = Reservation.confirm(reservation);

   
        resource.setReservedQuantity(
            resource.getReservedQuantity() - reservation.getQuantity()
        );

        resource.setConfirmedQuantity(
            resource.getConfirmedQuantity() + reservation.getQuantity()
        );


         return to_Response(confirmedReservation);
    }

    






    public List<ReservationResponse> list(){
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(this::to_Response).toList();
    }







    // CANCEL RESERVATION
    public ReservationResponse cancelReservation(UUID reservationId){
        //GET RESERVATION
        Reservation reservation = reservationRepository.findByIdForUpdate(  
            reservationId
        ).orElseThrow(
            ()-> new ReservationNotFoundException(
                "Reservation not found with id: " + reservationId
            )
        );

        // GET LOCKER RESOURCE
        Resource resource = resourceRepository
            .findByIdForUpdate(reservation.getResource().getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with key: " + reservation.getResource().getId()
            ));


            if(reservation.getStatus() != ReservationStatus.HELD){
                throw new IllegalArgumentException(
                    "Can not cancel a reservation with status " + reservation.getStatus() .toString()
                );
            }

            if(reservation.getQuantity() > resource.getReservedQuantity()){
                throw new IllegalArgumentException(
                       "Reservation quantity exceeds resource reserved quantity"
                );
            }

            resource.setReservedQuantity(
                resource.getReservedQuantity() - reservation.getQuantity()
            );

            Reservation canceledReservation = Reservation.cancel(reservation);

        return to_Response(canceledReservation);

    }










    public void expireReservation(UUID reservationId){
          //GET RESERVATION
        Reservation reservation = reservationRepository.findByIdForUpdate(  
            reservationId
        ).orElseThrow(
            ()-> new ReservationNotFoundException(
                "Reservation not found with id: " + reservationId
            )
        );

        // GET LOCKER RESOURCE
        Resource resource = resourceRepository
            .findByIdForUpdate(reservation.getResource().getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Resource not found with key: " + reservation.getResource().getId()
            ));


            if(reservation.getStatus() != ReservationStatus.HELD){
                return;
            }

             boolean isStillValid  = reservation.getExpiresAt().isAfter(
            Instant.now());

            if(isStillValid ){
                throw new ExpireReservationException(
                    "Reservation can not expired"
                );
            }

            if (reservation.getQuantity() > resource.getReservedQuantity()) {
                throw new IllegalStateException(
                    "Resource reserved quantity is insufficient"
                );
            }

            resource.setReservedQuantity(
                resource.getReservedQuantity() - reservation.getQuantity()
            );

            Reservation.expire(reservation);
        
    }





    private ReservationResponse to_Response(Reservation reservation){
        return new ReservationResponse(
            reservation.getId(),
            reservation.getResource().getResourceKey(),
            reservation.getQuantity(),
            reservation.getStatus(),
            reservation.getExpiresAt(),
            reservation.getCreatedAt()
        );
    }








}