
package com.alhourani.holdpoint.auth.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshCookieService {

    private static final String COOKIE_NAME = "refresh_token";

    @Value("${security.cookie.secure:false}")
    private boolean secure;

    @Value("${security.cookie.same-site:None}")
    private String sameSite;

    @Value("${security.cookie.refresh-token-ttl}")
    private Duration refreshTokenTtl;

    public ResponseCookie create(String refreshToken) {

        return ResponseCookie.from(COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/v1/auth")
                .maxAge(refreshTokenTtl)
                .build();
    }

    public ResponseCookie clear() {

        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build();
    }

    public String cookieName() {
        return COOKIE_NAME;
    }
}
