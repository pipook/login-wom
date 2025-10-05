package com.wom.login.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = true)
    private Instant locked_until;

    @Column(nullable = false)
    private boolean two_factor_enabled = false;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String two_factor_secret;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private int failed_attempts;
    private Instant created_at;
    private Instant updated_at;

    public UserModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getLocked_until() {
        return locked_until;
    }

    public void setLocked_until(Instant locked_until) {
        this.locked_until = locked_until;
    }

    public boolean isTwo_factor_enabled() {
        return two_factor_enabled;
    }

    public void setTwo_factor_enabled(boolean two_factor_enabled) {
        this.two_factor_enabled = two_factor_enabled;
    }

    public String getTwo_factor_secret() {
        return two_factor_secret;
    }

    public void setTwo_factor_secret(String two_factor_secret) {
        this.two_factor_secret = two_factor_secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFailed_attempts() {
        return failed_attempts;
    }

    public void setFailed_attempts(int failed_attempts) {
        this.failed_attempts = failed_attempts;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public Instant getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Instant updated_at) {
        this.updated_at = updated_at;
    }
}
