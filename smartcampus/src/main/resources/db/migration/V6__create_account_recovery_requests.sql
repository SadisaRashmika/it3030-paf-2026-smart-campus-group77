CREATE TABLE IF NOT EXISTS account_recovery_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(20) NOT NULL,
    student_email VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    issue_summary TEXT NOT NULL,
    id_photo_file_name VARCHAR(255) NOT NULL,
    id_photo_content_type VARCHAR(120) NOT NULL,
    id_photo_data_url TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_account_recovery_requests_status
    ON account_recovery_requests(status);

CREATE INDEX IF NOT EXISTS idx_account_recovery_requests_user_id
    ON account_recovery_requests(user_id);
