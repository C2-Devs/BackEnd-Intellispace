package com.intellispace.backend.identity.domain;

import java.util.UUID;

public class AppUser {
    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String displayName;

    private AppUser(UUID id, String email, String passwordHash, String displayName) {
        this.id = id;
        this.email = normalizeEmail(email);
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }

    public static AppUser register(String email, String passwordHash, String displayName) {
        return new AppUser(UUID.randomUUID(), email, passwordHash, displayName);
    }

    public static AppUser reconstruct(UUID id, String email, String passwordHash, String displayName) {
        return new AppUser(id, email, passwordHash, displayName);
    }

    private static String normalizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Not a valid email address: " + email);
        }
        return email.trim().toLowerCase();
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}