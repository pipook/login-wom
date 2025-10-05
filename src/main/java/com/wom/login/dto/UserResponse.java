package com.wom.login.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

public class UserResponse {
    private Long id;
    private boolean enabled = true;
    private String username;
    private String email;
    private Instant created_at;

    public UserResponse(Long id, boolean enabled, String username, String email, Instant createdAt) {
        this.id = id;
        this.enabled = enabled;
        this.username = username;
        this.email = email;
        this.created_at = createdAt;
    }
}
