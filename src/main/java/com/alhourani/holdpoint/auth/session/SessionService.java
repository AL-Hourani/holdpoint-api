package com.alhourani.holdpoint.auth.session;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alhourani.holdpoint.auth.dto.RotatedSession;
import com.alhourani.holdpoint.auth.dto.SessionResponse;
import com.alhourani.holdpoint.auth.security.SessionConfig;
import com.alhourani.holdpoint.common.exception.InvalidRefreshTokenException;
import com.alhourani.holdpoint.common.exception.SessionNotFoundException;
import com.alhourani.holdpoint.user.User;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SessionService {

    private final SessionRepository sessionRepository;
  
    @Value ("${security.jwt.refresh-token-ttl}")
    private Duration refreshTokenTtl;

    @Transactional
    public String createSession(User user) {

        // 1. Generate refresh token
        String refreshToken =
                SessionConfig.generateRefreshToken();

        // 2. Hash refresh token
        String hashToken =
                SessionConfig.hashToken(refreshToken);

        // 3. Calculate expiration
        Instant expiresAt =
                Instant.now().plus(refreshTokenTtl);

        // 4. Create session
        Session session = Session.create(
                user,
                hashToken,
                expiresAt
        );

        // 5. Save session
        sessionRepository.save(session);

        // 6. Return raw refresh token
        return refreshToken;
    }


    @Transactional
    public  RotatedSession rotateRefreshToken(String oldRefreshToken){

        if (oldRefreshToken == null || oldRefreshToken.isBlank()) {
                throw new InvalidRefreshTokenException();
        }
        // HASH OLD REFRESH TOKEN

        String oldTokenHash =
             SessionConfig.hashToken(oldRefreshToken);

                // 2. Find and lock the session
        Session session = sessionRepository
                .findByRefreshTokenHashForUpdate(oldTokenHash)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException()
                );


        // 3. Check session status
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new InvalidRefreshTokenException();
        }

            // 4. Check expiration
        if (session.getExpiresAt().isBefore(Instant.now())) {
            session.revoke();
            throw new InvalidRefreshTokenException();
        }

            // 5. Generate a new refresh token
        String newRefreshToken =
                SessionConfig.generateRefreshToken();

        // 6. Hash the new token
        String newTokenHash =
                SessionConfig.hashToken(newRefreshToken);

        // 7. Replace the old hash
        session.rotate(newTokenHash);

        // 8. Return the new refresh token
        return new RotatedSession(
                session.getUser().getId(),
                session.getUser().getFullName(),
                session.getUser().getEmail(),
                newRefreshToken
        );

    }





        @Transactional
        public void revokeSession(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
                return;
        }

        String tokenHash =
                SessionConfig.hashToken(refreshToken);

        Session session = sessionRepository
                .findByRefreshTokenHashForUpdate(tokenHash)
                .orElse(null);

        // Already invalid or unknown token
        if (session == null) {
                return;
        }

        session.revoke();
        }



        @Transactional
        public void revokeAllSessions(UUID userId) {

        // 1. Find all active sessions
        var sessions = sessionRepository
                .findAllByUser_IdAndStatus(
                        userId,
                        SessionStatus.ACTIVE
                );

        // 2. Revoke every session
        sessions.forEach(Session::revoke);
        }




        @Transactional(readOnly = true)
        public List<SessionResponse> getUserSessions(UUID userId) {

        return sessionRepository
                .findAllByUser_IdOrderByLastUsedAtDesc(userId)
                .stream()
                .map(session -> new SessionResponse(
                        session.getId(),
                        session.getStatus(),
                        session.getCreatedAt(),
                        session.getLastUsedAt(),
                        session.getExpiresAt(),
                        session.getRevokedAt()
                ))
                .toList();
        }


        @Transactional
        public void revokeSession(UUID userId, UUID sessionId) {

        Session session = sessionRepository
                .findByIdAndUserIdForUpdate(sessionId, userId)
                .orElseThrow(
                        () -> new SessionNotFoundException()
                );

        session.revoke();
        }




}
