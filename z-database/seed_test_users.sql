-- SmartCampus Test User Seeding Script
-- Use this to create inactive users for testing the OTP registration flow.
-- These users will have 'hashed_password' as their initial password (which they must change during activation).

INSERT INTO users (name, user_id, email, password, role, is_active, suspicious, otp_request_count, failed_otp_attempts, otp)
VALUES 
    -- Students
    ('Test Student 1', 'STU1001', 'stu1@example.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0, '123456'),
    ('Test Student 2', 'STU1002', 'stu2@example.com', 'hashed_password', 'STUDENT', FALSE, FALSE, 0, 0, '223344'),
    
    -- Lecturers
    ('Test Lecturer 1', 'LEC2001', 'lec1@example.com', 'hashed_password', 'LECTURER', FALSE, FALSE, 0, 0, '334455'),
    
    -- Timetable Manager
    ('System Manager', 'MGR3001', 'mgr1@example.com', 'hashed_password', 'TIMETABLE_MANAGER', FALSE, FALSE, 0, 0, '998877')
ON CONFLICT (email) DO UPDATE SET otp = EXCLUDED.otp;

-- Ensure roles are present (if not already seeded)
INSERT INTO roles (role_name) VALUES ('STUDENT'), ('LECTURER'), ('TIMETABLE_MANAGER'), ('ADMIN')
ON CONFLICT (role_name) DO NOTHING;
