CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    jti VARCHAR(128) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(512) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    last_used_at TIMESTAMP DEFAULT now(),
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    replaced_by_jti VARCHAR(128) NULL
);