package com.alhourani.holdpoint.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alhourani.holdpoint.auth.dto.RegisterRequest;
import com.alhourani.holdpoint.auth.dto.RegisterResponse;
import com.alhourani.holdpoint.auth.dto.RotatedSession;
import com.alhourani.holdpoint.auth.security.JwtService;
import com.alhourani.holdpoint.auth.session.SessionService;
import com.alhourani.holdpoint.auth.dto.LoginRequest;
import com.alhourani.holdpoint.auth.dto.LoginResponse;
import com.alhourani.holdpoint.auth.dto.MeResponse;
import com.alhourani.holdpoint.auth.dto.RefreshTokenRequest;
import com.alhourani.holdpoint.common.exception.DisabledException;
import com.alhourani.holdpoint.common.exception.EmailAlreadyExistsException;
import com.alhourani.holdpoint.common.exception.InvalidCredentialsException;
import com.alhourani.holdpoint.user.User;
import com.alhourani.holdpoint.user.UserRepository;
import com.alhourani.holdpoint.user.UserStatus;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional 
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;

    @Value ("${security.jwt.access-token-ttl}")
    private Duration accessTokenTtl;




    // REGISTER USER
    @Transactional 
    public RegisterResponse register(RegisterRequest request){

        // NORMALIZE EMAIL
        String email = request.email().trim().toLowerCase(Locale.ROOT);


        // CHECK WHETHER EMAIL ALREADY EXIST
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(
                 "An account with this email already exists"
            );
        }

        //Normalize full name
        String fullName = request.fullName().trim();

        int passwordBytes = request.password().getBytes(StandardCharsets.UTF_8).length;

        if (passwordBytes > 72) {
            throw new IllegalArgumentException(
                    "Password must not exceed 72 UTF-8 bytes"
            );
        }

        // 5. Hash password
        String passwordHash = passwordEncoder.encode(
                request.password()
        );

        // 6. Create user
        User user = User.create(
                fullName,
                email,
                passwordHash
        );


        // 7. Persist user
        User savedUser = userRepository.save(user);

        // 8. Return safe response
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getStatus(),
                savedUser.getCreatedAt()
        );
    } // register method



    @Transactional
    public LoginResponse login(LoginRequest request){
        // 1. Normalize email
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        authenticationManager.authenticate(
           new  UsernamePasswordAuthenticationToken(
                email,
                request.password()
            )
        );

            // 3. Retrieve user from database
    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
               new  InvalidCredentialsException("Invalid email or password")
            );

    // 4. Allow login only for ACTIVE accounts
    if (user.getStatus() != UserStatus.ACTIVE) {
        throw new DisabledException("Account is not active");
    }

    // 5. Generate access token
    String accessToken = jwtService.generateAccessToken(
            user.getId(),
            user.getEmail()
    );

    // 6. Create session and generate refresh token
        String refreshToken = sessionService.createSession(user);

    // 6. Return login response
    return new LoginResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            accessToken,
            refreshToken,
            "Bearer",
            accessTokenTtl.toSeconds()
    );

        
    }







        @Transactional(readOnly = true)
        public MeResponse getCurrentUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "User not found"
                        )
                );

        return new MeResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt()
        );
        }


        @Transactional
        public LoginResponse refresh(RefreshTokenRequest request) {

        // 1. Rotate the refresh token
        RotatedSession rotated =
                sessionService.rotateRefreshToken(
                        request.refreshToken()
                );

        // 2. Generate a new access token
        String accessToken =
                jwtService.generateAccessToken(
                        rotated.userId(),
                        rotated.email()
                );

        // 3. Return both new tokens
        return new LoginResponse(
                rotated.userId(),
                rotated.fullName(),
                rotated.email(),
                accessToken,
                rotated.refreshToken(),
                "Bearer",
                accessTokenTtl.toSeconds()
        );
        }




        public void logout(String refreshToken) {

        sessionService.revokeSession(refreshToken);
        }

        public void logoutAll(UUID userId) {

        sessionService.revokeAllSessions(userId);
        }



        public void logoutAllByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "User not found"
                        )
                );

        sessionService.revokeAllSessions(user.getId());
        }








        public UUID getUserIdByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "User not found"
                        )
                );
        }
        
        



}
