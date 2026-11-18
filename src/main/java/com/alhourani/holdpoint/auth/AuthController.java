package com.alhourani.holdpoint.auth;

import com.alhourani.holdpoint.auth.dto.LoginRequest;
import com.alhourani.holdpoint.auth.dto.LoginResponse;
import com.alhourani.holdpoint.auth.dto.MeResponse;
import com.alhourani.holdpoint.auth.dto.RefreshTokenRequest;
import com.alhourani.holdpoint.auth.dto.RegisterRequest;
import com.alhourani.holdpoint.auth.dto.RegisterResponse;
import com.alhourani.holdpoint.auth.dto.TokenResponse;
import com.alhourani.holdpoint.auth.security.RefreshCookieService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor 
public class AuthController {

    private final AuthService authService;
    private final RefreshCookieService refreshCookieService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    } // register method controller

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
         @Valid @RequestBody LoginRequest request
    ){
       LoginResponse result = authService.login(request);


       ResponseCookie cookie  = refreshCookieService.create(
                result.refreshToken()
       );


        TokenResponse response = new TokenResponse(
            result.accessToken(),
            result.tokenType(),
            result.expiresIn()
    );

     return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);

    }



        @PostMapping("/refresh")
        public ResponseEntity<TokenResponse> refresh(
                @CookieValue(
                        name = "refresh_token",
                        required = false
                ) String refreshToken
        ) {

        if (refreshToken == null || refreshToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .build();
        }

        LoginResponse result = authService.refresh(
                new RefreshTokenRequest(refreshToken)
        );

        ResponseCookie cookie = refreshCookieService.create(
                result.refreshToken()
        );

        TokenResponse response = new TokenResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
        }



        
        @PostMapping("/logout")
        public ResponseEntity<Void> logout(
                @CookieValue(
                        name = "refresh_token",
                        required = false
                ) String refreshToken
        ) {

        if (refreshToken != null && !refreshToken.isBlank()) {
                authService.logout(refreshToken);
        }

        ResponseCookie clearCookie = refreshCookieService.clear();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
        }





        @PostMapping("/logout-all")
        public ResponseEntity<Void> logoutAll(
                @AuthenticationPrincipal Jwt jwt
        ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        authService.logoutAll(userId);

        ResponseCookie clearCookie = refreshCookieService.clear();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
        }

        
        @GetMapping("/me")
        public MeResponse getCurrentUser(
                @AuthenticationPrincipal Jwt jwt
        ) {

                UUID userId = UUID.fromString(
                        jwt.getSubject()
                );

                return authService.getCurrentUser(userId);
        }




}


    

