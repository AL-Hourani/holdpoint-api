package com.alhourani.holdpoint.auth.security;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.access-token-ttl}")
    private java.time.Duration accessTokenTtl;


    public String generateAccessToken(UUID id  , String email){

        Instant now = Instant.now();
        Instant expireAt = now.plus(accessTokenTtl);

        JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("holdpoint")
        .subject(id.toString())
        .expiresAt(expireAt)
        .claim("email", email)
        .build();
        
        
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    
    }
}
