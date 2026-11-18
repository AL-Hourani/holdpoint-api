
package com.alhourani.holdpoint.reservation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 1000000)
    public void expireReservations() {

        List<UUID> expiredIds =
            reservationRepository.findExpiredReservationIds(
                ReservationStatus.HELD,
                Instant.now(),
                PageRequest.of(0, 100)
            );

        for (UUID reservationId : expiredIds) {

            try {
                reservationService.expireReservation(
                    reservationId
                );

            } catch (Exception exception) {

                log.error(
                    "Failed to expire reservation: {}",
                    reservationId,
                    exception
                );
            }
        }
    }
}
