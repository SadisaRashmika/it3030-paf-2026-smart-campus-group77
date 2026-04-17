ALTER TABLE users
    ADD COLUMN IF NOT EXISTS temporary_password_hash VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS temp_password_expires_at TIMESTAMP;
