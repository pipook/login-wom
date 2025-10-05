package com.wom.login.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String jti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @Column(nullable = false, length = 1024)
    private String token_hash;

    private Instant created_at;
    private Instant last_used_at;
    private Instant expiresAt;
    private boolean revoked;
    private String replaced_by_jti;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getToken_hash() {
        return token_hash;
    }

    public void setToken_hash(String token_hash) {
        this.token_hash = token_hash;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public Instant getLast_used_at() {
        return last_used_at;
    }

    public void setLast_used_at(Instant last_used_at) {
        this.last_used_at = last_used_at;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpires_at(Instant expires_at) {
        this.expiresAt = expires_at;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public String getReplaced_by_jti() {
        return replaced_by_jti;
    }

    public void setReplaced_by_jti(String replaced_by_jti) {
        this.replaced_by_jti = replaced_by_jti;
    }
}
