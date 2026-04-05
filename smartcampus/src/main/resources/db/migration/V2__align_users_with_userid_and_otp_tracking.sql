ALTER TABLE users ADD COLUMN IF NOT EXISTS user_id VARCHAR(20);

UPDATE users
SET user_id = CASE
    WHEN role = 'ADMIN' THEN 'ADMIN001'
    WHEN role = 'LECTURER' THEN CONCAT('LEC', LPAD(id::text, 3, '0'))
    WHEN role = 'STUDENT' THEN CONCAT('STU', LPAD(id::text, 3, '0'))
    ELSE CONCAT('USR', LPAD(id::text, 3, '0'))
END
WHERE user_id IS NULL OR user_id = '';

ALTER TABLE users ALTER COLUMN user_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'users_user_id_key'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT users_user_id_key UNIQUE (user_id);
    END IF;
END $$;

ALTER TABLE users ADD COLUMN IF NOT EXISTS otp_expires_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS suspicious BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS otp_request_count INTEGER DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_otp_attempts INTEGER DEFAULT 0;
