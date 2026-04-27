-- Member 1 Additional Real-World Features
-- This migration adds comprehensive booking, inventory, pricing, and notification systems

-- Create resource booking schedules table
CREATE TABLE resource_booking_schedules (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES enhanced_resources(id),
    user_id VARCHAR(255) NOT NULL,
    booking_title VARCHAR(255) NOT NULL,
    booking_description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    expected_attendees INTEGER,
    actual_attendees INTEGER,
    booking_status VARCHAR(50) DEFAULT 'PENDING',
    priority_level VARCHAR(50) DEFAULT 'NORMAL',
    total_cost DECIMAL(10,2),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    special_requirements TEXT,
    recurring_booking BOOLEAN DEFAULT FALSE,
    recurring_pattern VARCHAR(100),
    recurring_end_date TIMESTAMP,
    approval_notes TEXT,
    rejection_reason TEXT,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create resource inventory table
CREATE TABLE resource_inventory (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES enhanced_resources(id),
    item_name VARCHAR(255) NOT NULL,
    item_code VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    unit_of_measure VARCHAR(50) NOT NULL,
    current_stock INTEGER NOT NULL,
    minimum_stock INTEGER NOT NULL,
    maximum_stock INTEGER,
    reorder_level INTEGER NOT NULL,
    unit_cost DECIMAL(10,2),
    total_value DECIMAL(12,2),
    supplier_name VARCHAR(255),
    supplier_contact VARCHAR(255),
    last_restocked_date TIMESTAMP,
    next_restock_date TIMESTAMP,
    expiry_date TIMESTAMP,
    storage_location VARCHAR(255),
    condition_status VARCHAR(50) DEFAULT 'NEW',
    warranty_expiry TIMESTAMP,
    maintenance_required BOOLEAN DEFAULT FALSE,
    last_maintenance_date TIMESTAMP,
    next_maintenance_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create resource pricing table
CREATE TABLE resource_pricing (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES enhanced_resources(id),
    pricing_type VARCHAR(50) DEFAULT 'HOURLY',
    base_price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    price_per_unit DECIMAL(10,2),
    price_per_hour DECIMAL(10,2),
    price_per_day DECIMAL(10,2),
    price_per_week DECIMAL(10,2),
    price_per_month DECIMAL(10,2),
    minimum_booking_hours INTEGER,
    maximum_booking_hours INTEGER,
    deposit_required BOOLEAN DEFAULT FALSE,
    deposit_amount DECIMAL(10,2),
    cancellation_policy VARCHAR(50) DEFAULT 'STANDARD',
    cancellation_fee_percentage DECIMAL(5,2) DEFAULT 10.0,
    cancellation_fee_hours INTEGER DEFAULT 24,
    late_fee_percentage DECIMAL(5,2) DEFAULT 5.0,
    grace_period_minutes INTEGER DEFAULT 15,
    discount_available BOOLEAN DEFAULT FALSE,
    discount_percentage DECIMAL(5,2),
    discount_minimum_hours INTEGER,
    peak_hour_pricing BOOLEAN DEFAULT FALSE,
    peak_hour_multiplier DECIMAL(3,2) DEFAULT 1.5,
    peak_start_hour INTEGER,
    peak_end_hour INTEGER,
    weekend_pricing BOOLEAN DEFAULT FALSE,
    weekend_multiplier DECIMAL(3,2) DEFAULT 1.2,
    holiday_pricing BOOLEAN DEFAULT FALSE,
    holiday_multiplier DECIMAL(3,2) DEFAULT 1.3,
    seasonal_pricing BOOLEAN DEFAULT FALSE,
    seasonal_start_date TIMESTAMP,
    seasonal_end_date TIMESTAMP,
    seasonal_multiplier DECIMAL(3,2),
    bulk_discount_available BOOLEAN DEFAULT FALSE,
    bulk_discount_threshold INTEGER,
    bulk_discount_percentage DECIMAL(5,2),
    member_discount_available BOOLEAN DEFAULT FALSE,
    member_discount_percentage DECIMAL(5,2),
    student_discount_available BOOLEAN DEFAULT FALSE,
    student_discount_percentage DECIMAL(5,2),
    effective_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create resource notifications table
CREATE TABLE resource_notifications (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT REFERENCES enhanced_resources(id),
    user_id VARCHAR(255) NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    priority_level VARCHAR(50) DEFAULT 'NORMAL',
    status VARCHAR(50) DEFAULT 'UNREAD',
    delivery_method VARCHAR(50) DEFAULT 'IN_APP',
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    expires_at TIMESTAMP,
    action_required BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    action_button_text VARCHAR(100),
    related_booking_id BIGINT,
    related_maintenance_id BIGINT,
    related_rating_id BIGINT,
    auto_generated BOOLEAN DEFAULT FALSE,
    template_used VARCHAR(100),
    metadata TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    last_retry_at TIMESTAMP,
    delivery_status VARCHAR(50) DEFAULT 'PENDING',
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_resource_booking_schedules_resource ON resource_booking_schedules(resource_id);
CREATE INDEX idx_resource_booking_schedules_user ON resource_booking_schedules(user_id);
CREATE INDEX idx_resource_booking_schedules_status ON resource_booking_schedules(booking_status);
CREATE INDEX idx_resource_booking_schedules_start_time ON resource_booking_schedules(start_time);
CREATE INDEX idx_resource_booking_schedules_end_time ON resource_booking_schedules(end_time);
CREATE INDEX idx_resource_booking_schedules_priority ON resource_booking_schedules(priority_level);
CREATE INDEX idx_resource_booking_schedules_payment_status ON resource_booking_schedules(payment_status);
CREATE INDEX idx_resource_booking_schedules_recurring ON resource_booking_schedules(recurring_booking);

CREATE INDEX idx_resource_inventory_resource ON resource_inventory(resource_id);
CREATE INDEX idx_resource_inventory_item_code ON resource_inventory(item_code);
CREATE INDEX idx_resource_inventory_category ON resource_inventory(category);
CREATE INDEX idx_resource_inventory_condition ON resource_inventory(condition_status);
CREATE INDEX idx_resource_inventory_supplier ON resource_inventory(supplier_name);
CREATE INDEX idx_resource_inventory_location ON resource_inventory(storage_location);
CREATE INDEX idx_resource_inventory_stock ON resource_inventory(current_stock);
CREATE INDEX idx_resource_inventory_reorder ON resource_inventory(reorder_level);
CREATE INDEX idx_resource_inventory_expiry ON resource_inventory(expiry_date);
CREATE INDEX idx_resource_inventory_warranty ON resource_inventory(warranty_expiry);

CREATE INDEX idx_resource_pricing_resource ON resource_pricing(resource_id);
CREATE INDEX idx_resource_pricing_type ON resource_pricing(pricing_type);
CREATE INDEX idx_resource_pricing_currency ON resource_pricing(currency);
CREATE INDEX idx_resource_pricing_active ON resource_pricing(is_active);
CREATE INDEX idx_resource_pricing_effective_from ON resource_pricing(effective_from);
CREATE INDEX idx_resource_pricing_effective_to ON resource_pricing(effective_to);
CREATE INDEX idx_resource_pricing_base_price ON resource_pricing(base_price);

CREATE INDEX idx_resource_notifications_user ON resource_notifications(user_id);
CREATE INDEX idx_resource_notifications_resource ON resource_notifications(resource_id);
CREATE INDEX idx_resource_notifications_type ON resource_notifications(notification_type);
CREATE INDEX idx_resource_notifications_status ON resource_notifications(status);
CREATE INDEX idx_resource_notifications_priority ON resource_notifications(priority_level);
CREATE INDEX idx_resource_notifications_delivery ON resource_notifications(delivery_method);
CREATE INDEX idx_resource_notifications_scheduled ON resource_notifications(scheduled_at);
CREATE INDEX idx_resource_notifications_created ON resource_notifications(created_at);
CREATE INDEX idx_resource_notifications_action_required ON resource_notifications(action_required);

-- Insert sample booking schedules
INSERT INTO resource_booking_schedules (resource_id, user_id, booking_title, booking_description, start_time, end_time, expected_attendees, booking_status, priority_level, total_cost, payment_status) VALUES
(1, 'STUDENT001', 'Study Session - Physics Lab', 'Individual study session for physics coursework', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '2 hours', 1, 'APPROVED', 'NORMAL', 25.50, 'PAID'),
(2, 'STUDENT002', 'Group Meeting - Conference Room A', 'Team meeting for project discussion', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '3 hours', 5, 'APPROVED', 'NORMAL', 45.00, 'PAID'),
(3, 'LECTURER001', 'Guest Lecture - Auditorium', 'Guest lecture on advanced topics', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '4 hours', 150, 'APPROVED', 'HIGH', 200.00, 'PAID'),
(4, 'STUDENT003', 'Lab Practical - Computer Lab 201', 'Programming lab session', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '3 hours', 25, 'PENDING', 'NORMAL', 75.00, 'PENDING'),
(5, 'STUDENT004', 'Study Group - Library Study Area', 'Collaborative study session', CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP + INTERVAL '4 days' + INTERVAL '2 hours', 8, 'APPROVED', 'NORMAL', 0.00, 'PAID'),
(6, 'STUDENT005', 'Workout - Fitness Center', 'Personal fitness session', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '1 hour', 1, 'APPROVED', 'LOW', 15.00, 'PAID'),
(7, 'STUDENT006', 'Research - Biology Lab 301', 'Research experiment session', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '4 hours', 10, 'PENDING', 'NORMAL', 100.00, 'PENDING'),
(8, 'LECTURER002', 'Seminar - Main Hall 101', 'Faculty seminar presentation', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '5 days' + INTERVAL '3 hours', 80, 'APPROVED', 'HIGH', 0.00, 'PAID'),
(9, 'STUDENT007', 'Tutoring - Meeting Room 301', 'Peer tutoring session', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '1 hour', 3, 'APPROVED', 'NORMAL', 12.50, 'PAID'),
(10, 'STUDENT008', 'Event - Gymnasium', 'Sports club event', CURRENT_TIMESTAMP + INTERVAL '6 days', CURRENT_TIMESTAMP + INTERVAL '6 days' + INTERVAL '5 hours', 50, 'PENDING', 'NORMAL', 250.00, 'PENDING');

-- Insert sample inventory items
INSERT INTO resource_inventory (resource_id, item_name, item_code, description, category, unit_of_measure, current_stock, minimum_stock, maximum_stock, reorder_level, unit_cost, supplier_name, supplier_contact, storage_location, condition_status, warranty_expiry) VALUES
(2, 'Laptop Computer', 'COMP001', 'Dell laptop for programming', 'Electronics', 'units', 25, 5, 30, 8, 850.00, 'TechStore Inc', 'contact@techstore.com', 'Lab Storage', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '2 years'),
(2, 'Computer Mouse', 'MOUSE001', 'Wireless optical mouse', 'Electronics', 'units', 50, 10, 60, 15, 25.00, 'TechStore Inc', 'contact@techstore.com', 'Lab Storage', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '1 year'),
(2, 'USB Keyboard', 'KEYB001', 'Standard USB keyboard', 'Electronics', 'units', 30, 8, 40, 12, 35.00, 'TechStore Inc', 'contact@techstore.com', 'Lab Storage', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '1 year'),
(5, 'Basketball', 'BALL001', 'Official size 7 basketball', 'Sports Equipment', 'units', 8, 2, 10, 3, 45.00, 'SportsGear Co', 'info@sportsgear.com', 'Equipment Room', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '6 months'),
(5, 'Dumbbell Set', 'WEIGHT001', '20kg adjustable dumbbell set', 'Sports Equipment', 'sets', 5, 1, 8, 2, 120.00, 'SportsGear Co', 'info@sportsgear.com', 'Equipment Room', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '1 year'),
(5, 'Yoga Mat', 'MAT001', 'Non-slip exercise mat', 'Sports Equipment', 'units', 15, 5, 20, 8, 30.00, 'SportsGear Co', 'info@sportsgear.com', 'Equipment Room', 'FAIR', CURRENT_TIMESTAMP + INTERVAL '8 months'),
(11, 'Microphone', 'MIC001', 'Wireless microphone system', 'Audio Equipment', 'units', 4, 1, 6, 2, 150.00, 'AudioPro', 'sales@audiopro.com', 'AV Storage', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '1 year'),
(11, 'Projector Lamp', 'LAMP001', 'Replacement lamp for projectors', 'Audio Equipment', 'units', 10, 3, 15, 5, 80.00, 'AudioPro', 'sales@audiopro.com', 'AV Storage', 'NEW', CURRENT_TIMESTAMP + INTERVAL '3 months'),
(11, 'Extension Cable', 'CABLE001', '25ft power extension cord', 'Audio Equipment', 'units', 20, 5, 25, 8, 15.00, 'AudioPro', 'sales@audiopro.com', 'AV Storage', 'GOOD', CURRENT_TIMESTAMP + INTERVAL '6 months');

-- Insert sample pricing configurations
INSERT INTO resource_pricing (resource_id, pricing_type, base_price, currency, price_per_hour, price_per_day, minimum_booking_hours, maximum_booking_hours, deposit_required, deposit_amount, cancellation_policy, cancellation_fee_percentage, cancellation_fee_hours, late_fee_percentage, grace_period_minutes, discount_available, discount_percentage, discount_minimum_hours, peak_hour_pricing, peak_hour_multiplier, peak_start_hour, peak_end_hour, weekend_pricing, weekend_multiplier, member_discount_available, member_discount_percentage, student_discount_available, student_discount_percentage) VALUES
(1, 'HOURLY', 15.00, 'USD', 15.00, 120.00, 1, 8, TRUE, 50.00, 'STANDARD', 10.0, 24, 5.0, 15, TRUE, 20.0, 4, TRUE, 1.5, 9, 17, TRUE, 1.2, TRUE, 15.0, TRUE, 25.0),
(2, 'HOURLY', 25.00, 'USD', 25.00, 200.00, 1, 4, FALSE, 0.00, 'FLEXIBLE', 5.0, 12, 10.0, 10, FALSE, 0.0, 0, FALSE, 1.0, 0, 0, FALSE, 1.0, TRUE, 10.0, TRUE, 20.0),
(3, 'HOURLY', 20.00, 'USD', 20.00, 160.00, 1, 6, TRUE, 25.00, 'MODERATE', 15.0, 48, 5.0, 5, TRUE, 15.0, 3, FALSE, 1.0, 0, 0, FALSE, 1.0, FALSE, 0.0, FALSE, 0.0),
(4, 'PER_PERSON', 5.00, 'USD', 5.00, 40.00, 1, 24, FALSE, 0.00, 'FREE_CANCELLATION', 0.0, 0, 0.0, 30, FALSE, 0.0, 0, FALSE, 1.0, 0, 0, FALSE, 1.0, TRUE, 10.0, TRUE, 15.0),
(5, 'HOURLY', 10.00, 'USD', 10.00, 80.00, 1, 12, FALSE, 0.00, 'STANDARD', 10.0, 24, 5.0, 15, TRUE, 25.0, 6, TRUE, 1.3, 16, 20, TRUE, 1.25, TRUE, 20.0, TRUE, 30.0),
(6, 'HOURLY', 50.00, 'USD', 50.00, 400.00, 2, 8, TRUE, 100.00, 'STRICT', 25.0, 48, 10.0, 10, TRUE, 20.0, 4, TRUE, 1.8, 17, 21, TRUE, 1.5, TRUE, 15.0, FALSE, 0.0),
(7, 'HOURLY', 100.00, 'USD', 100.00, 800.00, 4, 12, TRUE, 200.00, 'STRICT', 25.0, 72, 15.0, 15, TRUE, 30.0, 8, FALSE, 1.0, 0, 0, FALSE, 1.0, FALSE, 0.0, FALSE, 0.0),
(8, 'HOURLY', 200.00, 'USD', 200.00, 1500.00, 1, 24, TRUE, 500.00, 'MODERATE', 20.0, 48, 10.0, 30, TRUE, 25.0, 6, TRUE, 2.0, 18, 22, TRUE, 1.3, TRUE, 20.0, TRUE, 25.0),
(9, 'HOURLY', 12.00, 'USD', 12.00, 96.00, 1, 4, FALSE, 0.00, 'FLEXIBLE', 5.0, 12, 0.0, 10, FALSE, 0.0, 0, FALSE, 1.0, 0, 0, FALSE, 1.0, TRUE, 10.0, TRUE, 15.0),
(10, 'PER_USE', 5.00, 'USD', 5.00, 40.00, 1, 1, FALSE, 0.00, 'STANDARD', 10.0, 24, 5.0, 15, TRUE, 20.0, 2, FALSE, 1.0, 0, 0, FALSE, 1.0, TRUE, 10.0, TRUE, 20.0),
(11, 'HOURLY', 75.00, 'USD', 75.00, 600.00, 2, 8, TRUE, 150.00, 'MODERATE', 15.0, 48, 7.5, 20, TRUE, 15.0, 4, TRUE, 1.5, 18, 21, FALSE, 1.0, TRUE, 12.0, TRUE, 18.0),
(12, 'PER_HOUR', 30.00, 'USD', 30.00, 240.00, 1, 6, FALSE, 0.00, 'STANDARD', 10.0, 24, 5.0, 15, FALSE, 0.0, 0, FALSE, 1.0, 0, 0, FALSE, 1.0, TRUE, 10.0, TRUE, 15.0),
(13, 'PER_HOUR', 35.00, 'USD', 35.00, 280.00, 1, 8, FALSE, 0.00, 'STANDARD', 10.0, 24, 5.0, 15, FALSE, 0.0, 0, FALSE, 1.0, 0, 0, FALSE, 1.0, TRUE, 10.0, TRUE, 15.0);

-- Insert sample notifications
INSERT INTO resource_notifications (user_id, notification_type, title, message, priority_level, delivery_method, action_required, related_booking_id, resource_id) VALUES
('STUDENT001', 'BOOKING_CONFIRMED', 'Booking Confirmed', 'Your booking ''Study Session - Physics Lab'' has been confirmed.', 'NORMAL', 'IN_APP', FALSE, 1, 2),
('STUDENT002', 'BOOKING_REMINDER', 'Booking Reminder', 'Reminder: You have a booking ''Group Meeting - Conference Room A'' coming up soon.', 'HIGH', 'IN_APP', FALSE, 2, 3),
('LECTURER001', 'BOOKING_APPROVED', 'Booking Approved', 'Your booking ''Guest Lecture - Auditorium'' has been approved.', 'NORMAL', 'IN_APP', FALSE, 3, 11),
('STUDENT003', 'PAYMENT_DUE', 'Payment Due', 'Payment of $75.00 is due for ''Lab Practical - Computer Lab 201''.', 'HIGH', 'IN_APP', TRUE, 4, 2),
('STUDENT004', 'SYSTEM_ALERT', 'System Alert', 'System maintenance scheduled for tonight at 11:00 PM.', 'URGENT', 'IN_APP', FALSE, NULL, NULL),
('STUDENT005', 'BOOKING_REJECTED', 'Booking Rejected', 'Your booking ''Study Group - Library Study Area'' has been rejected due to capacity limits.', 'HIGH', 'IN_APP', FALSE, 5, 4),
('LECTURER002', 'ANNOUNCEMENT', 'New Features Available', 'New resource management features are now available!', 'NORMAL', 'IN_APP', TRUE, NULL, NULL),
('TECH001', 'EQUIPMENT_ISSUE', 'Equipment Issue', 'There is an equipment issue with ''Main Hall 101''.', 'URGENT', 'IN_APP', TRUE, NULL, 1),
('STUDENT006', 'RATING_REQUEST', 'Rate Your Experience', 'Please rate your experience with ''Workout - Fitness Center''.', 'NORMAL', 'IN_APP', TRUE, 6, 5),
('STUDENT007', 'BOOKING_CANCELLED', 'Booking Cancelled', 'Your booking ''Research - Biology Lab 301'' has been cancelled.', 'NORMAL', 'IN_APP', FALSE, 7, 7),
('STUDENT008', 'SCHEDULE_CHANGE', 'Schedule Change', 'Your booking schedule has been updated.', 'NORMAL', 'IN_APP', TRUE, 8, 6),
('STUDENT009', 'ACCESS_GRANTED', 'Access Granted', 'You have been granted access to advanced resource management features.', 'NORMAL', 'IN_APP', TRUE, NULL, NULL),
('ADMIN001', 'SECURITY_ALERT', 'Security Alert', 'Unusual activity detected in resource booking system.', 'CRITICAL', 'IN_APP', TRUE, NULL, NULL),
('STUDENT010', 'ANNOUNCEMENT', 'System Update', 'System update completed successfully.', 'NORMAL', 'IN_APP', FALSE, NULL, NULL);

-- Add foreign key constraints for better data integrity
ALTER TABLE resource_booking_schedules 
ADD CONSTRAINT fk_booking_resource FOREIGN KEY (resource_id) REFERENCES enhanced_resources(id) ON DELETE CASCADE;

ALTER TABLE resource_inventory 
ADD CONSTRAINT fk_inventory_resource FOREIGN KEY (resource_id) REFERENCES enhanced_resources(id) ON DELETE CASCADE;

ALTER TABLE resource_pricing 
ADD CONSTRAINT fk_pricing_resource FOREIGN KEY (resource_id) REFERENCES enhanced_resources(id) ON DELETE CASCADE;

ALTER TABLE resource_notifications 
ADD CONSTRAINT fk_notification_resource FOREIGN KEY (resource_id) REFERENCES enhanced_resources(id) ON DELETE SET NULL;

-- Add check constraints for data validation
ALTER TABLE resource_booking_schedules 
ADD CONSTRAINT chk_booking_times CHECK (end_time > start_time),
ADD CONSTRAINT chk_booking_attendees CHECK (expected_attendees >= 0),
ADD CONSTRAINT chk_booking_actual_attendees CHECK (actual_attendees >= 0),
ADD CONSTRAINT chk_booking_cost CHECK (total_cost >= 0);

ALTER TABLE resource_inventory 
ADD CONSTRAINT chk_inventory_stock CHECK (current_stock >= 0),
ADD CONSTRAINT chk_inventory_min_stock CHECK (minimum_stock >= 0),
ADD CONSTRAINT chk_inventory_reorder CHECK (reorder_level >= 0),
ADD CONSTRAINT chk_inventory_max_stock CHECK (maximum_stock IS NULL OR current_stock <= maximum_stock),
ADD CONSTRAINT chk_inventory_unit_cost CHECK (unit_cost >= 0),
ADD CONSTRAINT chk_inventory_total_value CHECK (total_value >= 0);

ALTER TABLE resource_pricing 
ADD CONSTRAINT chk_pricing_base_price CHECK (base_price > 0),
ADD CONSTRAINT chk_pricing_per_hour CHECK (price_per_hour IS NULL OR price_per_hour > 0),
ADD CONSTRAINT chk_pricing_per_day CHECK (price_per_day IS NULL OR price_per_day > 0),
ADD CONSTRAINT chk_pricing_deposit CHECK (deposit_amount IS NULL OR deposit_amount >= 0),
ADD CONSTRAINT chk_pricing_cancellation_fee CHECK (cancellation_fee_percentage >= 0),
ADD CONSTRAINT chk_pricing_late_fee CHECK (late_fee_percentage >= 0),
ADD CONSTRAINT chk_pricing_grace_period CHECK (grace_period_minutes >= 0),
ADD CONSTRAINT chk_pricing_discount CHECK (discount_percentage IS NULL OR (discount_percentage >= 0 AND discount_percentage <= 100)),
ADD CONSTRAINT chk_pricing_peak_multiplier CHECK (peak_hour_multiplier IS NULL OR peak_hour_multiplier > 0),
ADD CONSTRAINT chk_pricing_weekend_multiplier CHECK (weekend_multiplier IS NULL OR weekend_multiplier > 0),
ADD CONSTRAINT chk_pricing_holiday_multiplier CHECK (holiday_multiplier IS NULL OR holiday_multiplier > 0),
ADD CONSTRAINT chk_pricing_seasonal_multiplier CHECK (seasonal_multiplier IS NULL OR seasonal_multiplier > 0),
ADD CONSTRAINT chk_pricing_bulk_discount CHECK (bulk_discount_percentage IS NULL OR (bulk_discount_percentage >= 0 AND bulk_discount_percentage <= 100)),
ADD CONSTRAINT chk_pricing_member_discount CHECK (member_discount_percentage IS NULL OR (member_discount_percentage >= 0 AND member_discount_percentage <= 100)),
ADD CONSTRAINT chk_pricing_student_discount CHECK (student_discount_percentage IS NULL OR (student_discount_percentage >= 0 AND student_discount_percentage <= 100)),
ADD CONSTRAINT chk_pricing_effective_dates CHECK (effective_to IS NULL OR effective_to > effective_from);

-- Create simple views for common queries
CREATE OR REPLACE VIEW booking_summary AS
SELECT 
    r.id as resource_id,
    r.name as resource_name,
    COUNT(b.id) as total_bookings
FROM enhanced_resources r
LEFT JOIN resource_booking_schedules b ON r.id = b.resource_id
GROUP BY r.id, r.name;

CREATE OR REPLACE VIEW inventory_summary AS
SELECT 
    r.id as resource_id,
    r.name as resource_name,
    COUNT(i.id) as total_items
FROM enhanced_resources r
LEFT JOIN resource_inventory i ON r.id = i.resource_id
GROUP BY r.id, r.name;
