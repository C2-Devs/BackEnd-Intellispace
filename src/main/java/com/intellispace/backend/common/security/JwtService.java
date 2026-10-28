package com.intellispace.backend.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {

    private final SecretKey signingKey;
    private final Duration expiration;
    private final JwtParser jwtParser;

    public JwtService(JwtProperties properties) {
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            throw new IllegalStateException(
                    "jwt.secret is required. Set JWT_SECRET or provide jwt.secret in application.properties.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(properties.getSecret());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "jwt.secret is not valid Base64. Generate one with: " +
                    "openssl rand -base64 32  (then set it as JWT_SECRET env var in production)", e);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret must decode to at least 32 bytes (256 bits) for HMAC-SHA256. " +
                    "Decoded length was " + keyBytes.length + " bytes.");
        }
        long expirationMinutes = properties.getExpirationMinutes();
        if (expirationMinutes <= 0) {
            throw new IllegalStateException(
                    "jwt.expiration-minutes must be greater than zero.");
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = Duration.ofMinutes(expirationMinutes);
        this.jwtParser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
    }

    public String issueToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    public UUID validateAndGetUserId(String token) {
        try {
            String subject = jwtParser
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("JWT subject is missing");
            }

            return UUID.fromString(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        }
    }
}