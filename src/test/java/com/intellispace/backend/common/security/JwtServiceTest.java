package com.intellispace.backend.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final byte[] SECRET_BYTES = new byte[32];
    private static final String SECRET =
            Base64.getEncoder().encodeToString(SECRET_BYTES);

    private static final SecretKey SIGNING_KEY =
            Keys.hmacShaKeyFor(SECRET_BYTES);

    private JwtService createService(long expirationMinutes) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setExpirationMinutes(expirationMinutes);
        return new JwtService(properties);
    }

    @Test
    void issuedTokenContainsExpectedUserAndExpiration() {
        JwtService service = createService(21_600);
        UUID userId = UUID.randomUUID();

        String token = service.issueToken(userId);

        assertEquals(userId, service.validateAndGetUserId(token));

        Claims claims = Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(userId.toString(), claims.getSubject());

        long lifetimeMinutes = Duration.between(
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant()
        ).toMinutes();

        assertEquals(21_600, lifetimeMinutes);
    }

    @Test
    void rejectsExpiredToken() {
        JwtService service = createService(21_600);
        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(now.minusSeconds(60)))
                .signWith(SIGNING_KEY)
                .compact();

        assertThrows(
                InvalidTokenException.class,
                () -> service.validateAndGetUserId(token)
        );
    }

    @Test
    void rejectsMalformedToken() {
        JwtService service = createService(21_600);

        assertThrows(
                InvalidTokenException.class,
                () -> service.validateAndGetUserId("not-a-valid-jwt")
        );
    }

    @Test
    void rejectsTokenWithInvalidUuidSubject() {
        JwtService service = createService(21_600);
        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject("not-a-uuid")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(300)))
                .signWith(SIGNING_KEY)
                .compact();

        assertThrows(
                InvalidTokenException.class,
                () -> service.validateAndGetUserId(token)
        );
    }

    @Test
    void rejectsTokenWithMissingSubject() {
        JwtService service = createService(21_600);
        Instant now = Instant.now();

        String token = Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(300)))
                .signWith(SIGNING_KEY)
                .compact();

        assertThrows(
                InvalidTokenException.class,
                () -> service.validateAndGetUserId(token)
        );
    }

    @Test
    void rejectsZeroExpiration() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setExpirationMinutes(0);

        assertThrows(
                IllegalStateException.class,
                () -> new JwtService(properties)
        );
    }

    @Test
    void rejectsNegativeExpiration() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setExpirationMinutes(-1);

        assertThrows(
                IllegalStateException.class,
                () -> new JwtService(properties)
        );
    }

    @Test
    void rejectsMissingSigningSecret() {
        JwtProperties properties = new JwtProperties();
        properties.setExpirationMinutes(21_600);

        assertThrows(
                IllegalStateException.class,
                () -> new JwtService(properties)
        );
    }

    @Test
    void rejectsWeakSigningSecret() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(
                Base64.getEncoder().encodeToString(new byte[16])
        );
        properties.setExpirationMinutes(21_600);

        assertThrows(
                IllegalStateException.class,
                () -> new JwtService(properties)
        );
    }
}
