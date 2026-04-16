BEGIN;

-- Drop dependent objects first
DROP TABLE IF EXISTS lecturer_work_assignment_recipients CASCADE;
DROP TABLE IF EXISTS lecturer_work_assignments CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS admin_user_roles CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop old trigger/function if they exist
DROP TRIGGER IF EXISTS update_users_timestamp ON users;
DROP FUNCTION IF EXISTS update_timestamp();

-- Recreate tables
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    otp VARCHAR(6),
    otp_expires_at TIMESTAMP,
    suspicious BOOLEAN DEFAULT FALSE,
    otp_request_count INTEGER DEFAULT 0,
    failed_otp_attempts INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    technician_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'Open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tickets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tickets_technician FOREIGN KEY (technician_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_resource FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE admin_user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(100) NOT NULL,
    CONSTRAINT fk_admin_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE lecturer_work_assignments (
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

CREATE TABLE lecturer_work_assignment_recipients (
    assignment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (assignment_id, user_id),
    CONSTRAINT fk_assignment_recipients_assignment FOREIGN KEY (assignment_id)
        REFERENCES lecturer_work_assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_recipients_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_assignment_recipients_user_id
    ON lecturer_work_assignment_recipients(user_id);

-- Timestamp trigger
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_timestamp
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Seed roles
INSERT INTO roles (name) VALUES
('ADMIN'),
('LECTURER'),
('STUDENT');

-- Seed requested admin user
-- Password is plain 12345 as requested
INSERT INTO users (
    name, user_id, email, password, role, is_active, suspicious, otp_request_count, failed_otp_attempts
) VALUES (
    'Sadisa Rashmika', 'ADMIN001', 'koffykoffy@gmail.com', '12345', 'ADMIN', TRUE, FALSE, 0, 0
);

-- Insert 5 Lecturers
INSERT INTO users (name, user_id, email, password, role, is_active, suspicious, otp_request_count, failed_otp_attempts)
VALUES 
('Dr. Prabash Perera', 'LEC001', 'xxxxxxxxx@gmail.com', 'hashed_password', 'LECTURER', FALSE, FALSE, 0, 0),  -- Not activated yet
('Dr. Nuwan Thushara', 'LEC002', 'xxxxxxxxx@gmail.com', 'hashed_password', 'LECTURER', FALSE, FALSE, 0, 0),  -- Not activated yet
('Dr. Saman Karunarathne', 'LEC003', 'lecturer3@example.com', 'hashed_password', 'LECTURER', FALSE, FALSE, 0, 0),  -- Not activated yet
('Dr. Nimesh Silva', 'LEC004', 'lecturer4@example.com', 'hashed_password', 'LECTURER', FALSE, FALSE, 0, 0),  -- Not activated yet
('Dr. Amara Kumara', 'LEC005', 'lecturer5@example.com', 'hashed_password', 'LECTURER', FALSE, FALSE, 0, 0);  -- Not activated yet

-- Insert 5 Students
INSERT INTO users (name, user_id, email, password, role, is_active, suspicious, otp_request_count, failed_otp_attempts)
VALUES 
('Kasun Kalhara', 'STU001', 'xxxxxxxx@gmail.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0),  -- Not activated yet
('Sumudu Perera', 'STU002', 'xxxxxxxx@gmail.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0),  -- Not activated yet
('Sithmi Amarasiri', 'STU003', 'student3@example.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0),  -- Not activated yet
('Vindiya Perera', 'STU004', 'student4@example.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0),  -- Not activated yet
('Senal Galagedara', 'STU005', 'student5@example.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0);  -- Not activated yet


COMMIT;


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
