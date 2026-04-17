ALTER TABLE bookings ADD COLUMN purpose VARCHAR(255);
ALTER TABLE bookings ADD COLUMN expected_attendees INTEGER;
ALTER TABLE bookings ADD COLUMN admin_notes TEXT;
