
package com.alhourani.holdpoint.reservation;

import com.alhourani.holdpoint.resource.Resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(length = 150)
    private String reference;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }


    public static Reservation createHold(
            Resource resource,
            Long quantity
    ) {
        if (resource == null) {
            throw new IllegalArgumentException(
                "Resource cannot be null"
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero"
            );
        }

        Reservation reservation = new Reservation();

        reservation.setResource(resource);
        reservation.setQuantity(quantity);
        reservation.setStatus(ReservationStatus.HELD);
        reservation.setExpiresAt(
            Instant.now().plus(Duration.ofMinutes(30))
        );

        return reservation;
    }


    public static Reservation confirm(Reservation reservation){
        if(reservation.getStatus() != ReservationStatus.HELD){
          throw new IllegalArgumentException(
            "Reservation status must be held to confirmed"
          );
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservation;
    }

    public static Reservation cancel(Reservation reservation){
        if(reservation.getStatus() != ReservationStatus.HELD){
          throw new IllegalArgumentException(
            "Reservation status must be held to cancel"
          );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservation;
    }



    public static Reservation expire(Reservation reservation){
        if(reservation.getStatus() != ReservationStatus.HELD){
          throw new IllegalArgumentException(
            "Reservation status must be held to expired"
          );
        }

        reservation.setStatus(ReservationStatus.EXPIRED);
        return reservation;
    }


    

}
