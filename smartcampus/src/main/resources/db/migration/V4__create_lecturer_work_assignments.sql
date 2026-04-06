CREATE TABLE IF NOT EXISTS lecturer_work_assignments (
    id BIGSERIAL PRIMARY KEY,
    work_title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    start_date DATE,
    end_date DATE,
    start_time TIME,
    end_time TIME,
    send_email BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lecturer_work_assignment_recipients (
    assignment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (assignment_id, user_id),
    CONSTRAINT fk_assignment_recipients_assignment FOREIGN KEY (assignment_id)
        REFERENCES lecturer_work_assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_recipients_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_assignment_recipients_user_id
    ON lecturer_work_assignment_recipients(user_id);