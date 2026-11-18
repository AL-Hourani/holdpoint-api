
package com.alhourani.holdpoint.auth.session;

import java.time.Instant;
import java.util.UUID;

import com.alhourani.holdpoint.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter 
@Setter
@Table(
    name = "sessions",
    indexes = {
        @Index(name = "idx_sessions_user_id", columnList = "user_id"),
        @Index(name = "idx_sessions_expires_at", columnList = "expires_at")
    }
)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(
        name = "refresh_token_hash",
        nullable = false,
        unique = true,
        length = 64
    )
    private String refreshTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

  
  
    @Column(name = "expires_at", nullable = false , length = 20)
    private Instant expiresAt;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    protected void onCreate() {

        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;
        this.lastUsedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }


    public void rotate(String newTokenHash) {
        this.refreshTokenHash = newTokenHash;
        this.lastUsedAt = Instant.now();
    }


    public void revoke(){
        
        if (this.status == SessionStatus.REVOKED) {
            return;
        }

        this.status = SessionStatus.REVOKED;
        this.revokedAt = Instant.now();
    }

   
    public static Session create(
            User user,
            String hash,
            Instant expiresAt
    ) {
        Session session = new Session();

        session.setUser(user);
        session.setRefreshTokenHash(hash);
        session.setStatus(SessionStatus.ACTIVE);
        session.setExpiresAt(expiresAt);

        return session;
    }


    // Getters and Setters
}
