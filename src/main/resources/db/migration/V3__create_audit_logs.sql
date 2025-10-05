CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    event VARCHAR(100) NOT NULL,
    details TEXT,
    ip VARCHAR(50),
    created_at TIMESTAMP DEFAULT now()
);