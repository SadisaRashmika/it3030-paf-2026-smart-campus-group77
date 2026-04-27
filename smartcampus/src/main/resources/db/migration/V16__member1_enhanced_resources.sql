-- Member 1 Enhanced Resource Management System

-- Create resource categories table
CREATE TABLE resource_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    color VARCHAR(50) NOT NULL,
    icon_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create resource locations table
CREATE TABLE resource_locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    building_name VARCHAR(255) NOT NULL,
    floor_number INTEGER,
    room_number VARCHAR(50),
    capacity INTEGER,
    has_projector BOOLEAN DEFAULT FALSE,
    has_wifi BOOLEAN DEFAULT TRUE,
    has_air_conditioning BOOLEAN DEFAULT FALSE,
    is_accessible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create enhanced resources table
CREATE TABLE enhanced_resources (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL REFERENCES resource_categories(id),
    location_id BIGINT NOT NULL REFERENCES resource_locations(id),
    type VARCHAR(100) NOT NULL,
    resource_code VARCHAR(50) NOT NULL UNIQUE,
    available BOOLEAN DEFAULT TRUE,
    max_capacity INTEGER,
    current_capacity INTEGER DEFAULT 0,
    equipment_list TEXT,
    usage_rules TEXT,
    maintenance_status VARCHAR(50) DEFAULT 'GOOD',
    last_maintenance_date TIMESTAMP,
    next_maintenance_date TIMESTAMP,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    total_ratings INTEGER DEFAULT 0,
    usage_count INTEGER DEFAULT 0,
    booking_count INTEGER DEFAULT 0,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create resource ratings table
CREATE TABLE resource_ratings (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES enhanced_resources(id),
    user_id VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(resource_id, user_id)
);

-- Create resource maintenance table
CREATE TABLE resource_maintenance (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES enhanced_resources(id),
    maintenance_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    scheduled_date TIMESTAMP NOT NULL,
    completed_date TIMESTAMP,
    technician_id VARCHAR(255),
    technician_name VARCHAR(255),
    cost DECIMAL(10,2),
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create resource analytics table
CREATE TABLE resource_analytics (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES enhanced_resources(id),
    date_recorded TIMESTAMP NOT NULL,
    total_bookings INTEGER DEFAULT 0,
    total_usage_hours DECIMAL(8,2) DEFAULT 0.00,
    peak_usage_hour INTEGER,
    average_booking_duration DECIMAL(8,2) DEFAULT 0.00,
    cancellation_count INTEGER DEFAULT 0,
    revenue_generated DECIMAL(10,2) DEFAULT 0.00,
    maintenance_downtime_hours DECIMAL(8,2) DEFAULT 0.00,
    user_satisfaction_score DECIMAL(3,2) DEFAULT 0.00,
    utilization_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_resource_categories_name ON resource_categories(name);
CREATE INDEX idx_resource_locations_building_floor ON resource_locations(building_name, floor_number);
CREATE INDEX idx_enhanced_resources_category ON enhanced_resources(category_id);
CREATE INDEX idx_enhanced_resources_location ON enhanced_resources(location_id);
CREATE INDEX idx_enhanced_resources_available ON enhanced_resources(available);
CREATE INDEX idx_enhanced_resources_code ON enhanced_resources(resource_code);
CREATE INDEX idx_enhanced_resources_rating ON enhanced_resources(average_rating);
CREATE INDEX idx_resource_ratings_resource ON resource_ratings(resource_id);
CREATE INDEX idx_resource_ratings_user ON resource_ratings(user_id);
CREATE INDEX idx_resource_maintenance_resource ON resource_maintenance(resource_id);
CREATE INDEX idx_resource_maintenance_status ON resource_maintenance(status);
CREATE INDEX idx_resource_maintenance_scheduled ON resource_maintenance(scheduled_date);
CREATE INDEX idx_resource_analytics_resource ON resource_analytics(resource_id);
CREATE INDEX idx_resource_analytics_date ON resource_analytics(date_recorded);

-- Insert default resource categories
INSERT INTO resource_categories (name, description, color, icon_name) VALUES
('Classroom', 'Traditional classroom spaces for lectures and tutorials', '#3B82F6', 'chalkboard'),
('Laboratory', 'Science and computer labs for practical sessions', '#10B981', 'flask'),
('Meeting Room', 'Conference and meeting rooms for discussions', '#F59E0B', 'users'),
('Library', 'Library study areas and resource centers', '#8B5CF6', 'book'),
('Sports Facility', 'Gymnasium, sports fields, and fitness centers', '#EF4444', 'heart'),
('Auditorium', 'Large halls for presentations and events', '#6366F1', 'microphone'),
('Computer Lab', 'Specialized computer facilities', '#14B8A6', 'computer'),
('Study Room', 'Quiet study spaces for individual or group work', '#84CC16', 'book-open');

-- Insert default resource locations
INSERT INTO resource_locations (name, description, building_name, floor_number, room_number, capacity, has_projector, has_wifi, has_air_conditioning, is_accessible) VALUES
('Main Hall 101', 'Large lecture hall', 'Main Building', 1, '101', 200, TRUE, TRUE, TRUE, TRUE),
('Computer Lab 201', 'Computer laboratory', 'Main Building', 2, '201', 50, TRUE, TRUE, TRUE, TRUE),
('Meeting Room 301', 'Small meeting room', 'Main Building', 3, '301', 15, TRUE, TRUE, FALSE, TRUE),
('Library Study Area', 'Quiet study area', 'Main Building', 4, '401', 100, FALSE, TRUE, TRUE, TRUE),
('Physics Lab 101', 'Physics laboratory', 'Science Building', 1, '101', 30, FALSE, TRUE, FALSE, TRUE),
('Chemistry Lab 201', 'Chemistry laboratory', 'Science Building', 2, '201', 25, FALSE, TRUE, FALSE, TRUE),
('Biology Lab 301', 'Biology laboratory', 'Science Building', 3, '301', 35, FALSE, TRUE, FALSE, TRUE),
('Gymnasium', 'Indoor sports facility', 'Sports Complex', 1, 'Gym', 150, FALSE, TRUE, TRUE, TRUE),
('Fitness Center', 'Weight training area', 'Sports Complex', 1, 'Fitness', 40, FALSE, TRUE, TRUE, TRUE),
('Swimming Pool', 'Olympic size pool', 'Sports Complex', 2, 'Pool', 200, FALSE, TRUE, TRUE, TRUE),
('Auditorium', 'Main auditorium', 'Administration Building', 1, 'Auditorium', 500, TRUE, TRUE, TRUE, TRUE),
('Conference Room A', 'Large conference room', 'Administration Building', 2, 'A', 30, TRUE, TRUE, TRUE, TRUE),
('Conference Room B', 'Small conference room', 'Administration Building', 2, 'B', 20, TRUE, TRUE, FALSE, TRUE);

-- Insert sample enhanced resources
INSERT INTO enhanced_resources (name, description, category_id, location_id, type, resource_code, max_capacity, equipment_list, usage_rules, image_url) VALUES
('Main Lecture Hall', 'Spacious lecture hall with modern audio-visual equipment', 1, 1, 'Classroom', 'RES1234567890', 200, 'Projector, Whiteboard, Sound System, WiFi', 'No food or drinks allowed, Clean up after use', 'https://example.com/images/main-hall.jpg'),
('Advanced Computer Lab', 'State-of-the-art computer laboratory with high-performance workstations', 7, 2, 'Computer Lab', 'RES1234567891', 50, '30 Computers, Projector, Whiteboard, High-speed Internet', 'No external software installation, Save work to network drive', 'https://example.com/images/computer-lab.jpg'),
('Executive Meeting Room', 'Professional meeting room for executive discussions', 3, 3, 'Meeting Room', 'RES1234567892', 15, 'Conference Table, Projector, Video Conferencing, Coffee Machine', 'Book in advance, Clean up after meeting', 'https://example.com/images/meeting-room.jpg'),
('Silent Study Zone', 'Quiet area for focused individual study', 8, 4, 'Study Room', 'RES1234567893', 100, 'Study Carrels, Power Outlets, WiFi, Reference Books', 'Complete silence required, No phone calls', 'https://example.com/images/study-zone.jpg'),
('Physics Research Lab', 'Advanced physics laboratory for experimental research', 2, 5, 'Laboratory', 'RES1234567894', 30, 'Lab Equipment, Safety Gear, Computers, Storage', 'Safety gear required, Follow lab protocols', 'https://example.com/images/physics-lab.jpg'),
('Chemistry Analysis Lab', 'Modern chemistry laboratory for analytical experiments', 2, 6, 'Laboratory', 'RES1234567895', 25, 'Fume Hoods, Lab Equipment, Safety Equipment, Storage', 'Safety training required, Proper waste disposal', 'https://example.com/images/chemistry-lab.jpg'),
('Biology Research Lab', 'Biology laboratory for biological research and experiments', 2, 7, 'Laboratory', 'RES1234567896', 35, 'Microscopes, Lab Equipment, Safety Gear, Storage', 'Biosafety protocols required, Proper specimen handling', 'https://example.com/images/biology-lab.jpg'),
('Indoor Sports Arena', 'Multi-purpose indoor sports facility', 5, 8, 'Sports Facility', 'RES1234567897', 150, 'Basketball Court, Volleyball Net, Scoreboard, Changing Rooms', 'Proper sports attire required, Book in advance', 'https://example.com/images/sports-arena.jpg'),
('Modern Fitness Center', 'Well-equipped fitness center with cardio and weight training equipment', 5, 9, 'Sports Facility', 'RES1234567898', 40, 'Treadmills, Weights, Exercise Machines, Showers', 'Clean equipment after use, Proper workout attire', 'https://example.com/images/fitness-center.jpg'),
('Olympic Swimming Pool', 'Full-size Olympic swimming pool for competitive and recreational swimming', 5, 10, 'Sports Facility', 'RES1234567899', 200, 'Olympic Pool, Diving Board, Changing Rooms, Showers', 'Swimwear required, Shower before entering pool', 'https://example.com/images/swimming-pool.jpg'),
('Grand Auditorium', 'Large auditorium for presentations, performances, and events', 6, 11, 'Auditorium', 'RES1234567900', 500, 'Stage, Sound System, Lighting, Projection Screen', 'No food or drinks in seating area, Respect event etiquette', 'https://example.com/images/auditorium.jpg'),
('Board Conference Room', 'Executive conference room for board meetings and presentations', 3, 12, 'Meeting Room', 'RES1234567901', 30, 'Large Conference Table, Video Conferencing, Presentation Equipment', 'Formal business attire preferred, Book in advance', 'https://example.com/images/board-room.jpg'),
('Team Meeting Room', 'Cozy meeting room for team discussions and brainstorming', 3, 13, 'Meeting Room', 'RES1234567902', 20, 'Round Table, Whiteboard, Coffee Machine, Video Conferencing', 'Collaborative environment, Clean up after use', 'https://example.com/images/team-room.jpg');

-- Insert sample resource ratings
INSERT INTO resource_ratings (resource_id, user_id, rating, review_text) VALUES
(1, 'STUDENT001', 5, 'Excellent facility with great equipment and comfortable seating'),
(1, 'STUDENT002', 4, 'Good space but can get crowded during peak hours'),
(2, 'STUDENT003', 5, 'Modern computers and fast internet, perfect for programming'),
(2, 'STUDENT004', 4, 'Great lab but needs more power outlets'),
(3, 'LECTURER001', 5, 'Professional environment with excellent video conferencing'),
(4, 'STUDENT005', 5, 'Perfect quiet space for studying, very peaceful'),
(5, 'STUDENT006', 4, 'Good equipment but lab safety protocols are strict'),
(8, 'STUDENT007', 5, 'Amazing sports facility, well-maintained equipment'),
(9, 'STUDENT008', 4, 'Good variety of equipment but can get busy in evenings'),
(11, 'LECTURER002', 5, 'Outstanding auditorium with excellent acoustics and technology');

-- Insert sample maintenance records
INSERT INTO resource_maintenance (resource_id, maintenance_type, description, scheduled_date, technician_id, technician_name, status, notes) VALUES
(2, 'ROUTINE', 'Monthly computer maintenance and software updates', CURRENT_TIMESTAMP + INTERVAL '7 days', 'TECH001', 'John Smith', 'SCHEDULED', 'Check all computers for updates'),
(5, 'INSPECTION', 'Quarterly safety equipment inspection', CURRENT_TIMESTAMP + INTERVAL '14 days', 'TECH002', 'Sarah Johnson', 'SCHEDULED', 'Inspect all safety gear and lab equipment'),
(8, 'REPAIR', 'Fix basketball hoop and check court markings', CURRENT_TIMESTAMP + INTERVAL '3 days', 'TECH003', 'Mike Wilson', 'SCHEDULED', 'Court maintenance and equipment repair'),
(9, 'ROUTINE', 'Monthly fitness equipment maintenance', CURRENT_TIMESTAMP + INTERVAL '10 days', 'TECH004', 'Emily Brown', 'SCHEDULED', 'Inspect and service all exercise machines'),
(11, 'INSPECTION', 'Annual fire safety and emergency systems check', CURRENT_TIMESTAMP + INTERVAL '30 days', 'TECH005', 'David Lee', 'SCHEDULED', 'Complete safety systems inspection');

-- Insert sample analytics data
INSERT INTO resource_analytics (resource_id, date_recorded, total_bookings, total_usage_hours, peak_usage_hour, average_booking_duration, cancellation_count, revenue_generated, user_satisfaction_score, utilization_rate) VALUES
(1, CURRENT_DATE - INTERVAL '1 day', 12, 48.5, 14, 4.0, 1, 1200.00, 4.5, 75.2),
(2, CURRENT_DATE - INTERVAL '1 day', 8, 32.0, 10, 4.0, 0, 800.00, 4.7, 64.0),
(3, CURRENT_DATE - INTERVAL '1 day', 6, 18.0, 15, 3.0, 1, 600.00, 4.8, 45.0),
(4, CURRENT_DATE - INTERVAL '1 day', 25, 125.0, 16, 5.0, 2, 1250.00, 4.6, 85.0),
(8, CURRENT_DATE - INTERVAL '1 day', 10, 40.0, 18, 4.0, 0, 1000.00, 4.4, 60.0),
(9, CURRENT_DATE - INTERVAL '1 day', 15, 60.0, 17, 4.0, 1, 1500.00, 4.3, 72.0),
(11, CURRENT_DATE - INTERVAL '1 day', 3, 15.0, 13, 5.0, 0, 1500.00, 4.9, 30.0);
