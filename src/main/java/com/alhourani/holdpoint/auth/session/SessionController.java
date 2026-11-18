package com.alhourani.holdpoint.auth.session;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alhourani.holdpoint.auth.AuthService;
import com.alhourani.holdpoint.auth.dto.SessionResponse;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final AuthService authService;


     // GET USER SESSIONS
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getSessions(
            Authentication authentication
    ) {

        UUID userId = authService.getUserIdByEmail(
                authentication.getName()
        );

        return ResponseEntity.ok(
                sessionService.getUserSessions(userId)
        );
    }

    // REVOKE SPECIFIC SESSION
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> revokeSession(
            @PathVariable UUID sessionId,
            Authentication authentication
    ) {

        UUID userId = authService.getUserIdByEmail(
                authentication.getName()
        );

        sessionService.revokeSession(userId, sessionId);

        return ResponseEntity.noContent().build();
    }
}
