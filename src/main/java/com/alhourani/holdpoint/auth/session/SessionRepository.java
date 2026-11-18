package com.alhourani.holdpoint.auth.session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface SessionRepository extends  JpaRepository<Session , UUID>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Session s WHERE s.refreshTokenHash = :hash")
    Optional<Session> findByRefreshTokenHashForUpdate(
        @Param("hash") String hash
    );


    List<Session> findAllByUser_IdAndStatus(
        UUID userId,
        SessionStatus status
    );


    List<Session> findAllByUser_IdOrderByLastUsedAtDesc(
        UUID userId
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM Session s
        WHERE s.id = :sessionId
        AND s.user.id = :userId
    """)
    Optional<Session> findByIdAndUserIdForUpdate(
            @Param("sessionId") UUID sessionId,
            @Param("userId") UUID userId
    );

    
}
