package com.wom.login.security;

import java.time.Instant;

public class RefreshTokenPair {
    private final String jti;
    private final String token;
    private final Instant expiresAt;

    public RefreshTokenPair(String jti, String token, Instant expiresAt) {
        this.jti = jti;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getJti() {
        return jti;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
