package com.alhourani.holdpoint.reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.Instant;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ReservationRepository extends JpaRepository<Reservation , UUID> {
    
    List<Reservation> findAllByResource_Id(UUID resourceId);

    List<Reservation> findAllByStatusAndExpiresAtBefore(
        ReservationStatus status,
        Instant time
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.id = :reservationId
    """)
    Optional<Reservation> findByIdForUpdate(
        @Param("reservationId") UUID reservationId
    );


    @Query("""
            SELECT r.id
            FROM Reservation r
            WHERE r.status = :status
            AND r.expiresAt <= :now
            ORDER BY r.expiresAt ASC
        """)
        List<UUID> findExpiredReservationIds(
            @Param("status") ReservationStatus status,
            @Param("now") Instant now,
            Pageable pageable
        );


}
