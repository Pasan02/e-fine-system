-- =============================================================================
-- V1__create_users_table.sql
-- Creates the USERS table as defined in the ER diagram (Section 4.1).
-- Managed by Member 6 (Auth module) but defined here since Member 1 owns migrations.
-- =============================================================================

CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL     PRIMARY KEY,
    username      VARCHAR(50)   NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    full_name     VARCHAR(100)  NOT NULL,
    phone_number  VARCHAR(20)   NOT NULL,
    role          VARCHAR(20)   NOT NULL CHECK (role IN ('ADMIN', 'OFFICER', 'DRIVER')),
    district      VARCHAR(50),
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Index for username lookups (JWT auth)
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
-- Index for filtering officers by district (admin reporting)
CREATE INDEX IF NOT EXISTS idx_users_district ON users (district);

COMMENT ON TABLE  users              IS 'System users: ADMIN, OFFICER, DRIVER roles';
COMMENT ON COLUMN users.role         IS 'ADMIN | OFFICER | DRIVER';
COMMENT ON COLUMN users.district     IS 'Police district the officer is assigned to';
COMMENT ON COLUMN users.password_hash IS 'BCrypt-hashed password (never stored in plain text)';
