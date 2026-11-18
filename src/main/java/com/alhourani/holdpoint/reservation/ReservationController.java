package com.alhourani.holdpoint.reservation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alhourani.holdpoint.reservation.dto.CreateHold;
import com.alhourani.holdpoint.reservation.dto.ReservationResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController 
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor 
public class ReservationController {

    private final ReservationService reservationService;
    // CREATE HOLD
    @PostMapping
    public ResponseEntity<ReservationResponse> createHold(
            @RequestBody CreateHold request
    ) {

        ReservationResponse response =
                reservationService.createHold(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // CONFIRM RESERVATION
    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(
            @PathVariable UUID reservationId
    ) {

        ReservationResponse response =
                reservationService.confirmReservation(reservationId);

        return ResponseEntity.ok(response);
    }

    // CANCEL RESERVATION
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable UUID reservationId
    ) {

        ReservationResponse response =
                reservationService.cancelReservation(reservationId);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<ReservationResponse>> list() {
        List<ReservationResponse> reservations = reservationService.list();
        return ResponseEntity.ok(reservations);
    }
    
}
