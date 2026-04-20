-- Add Member 2 specific fields to the existing bookings table
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS purpose VARCHAR(500);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS expected_attendees INTEGER;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

-- Create an index to optimize booking conflict checks (resource + approved status)
CREATE INDEX IF NOT EXISTS idx_bookings_resource_status
    ON bookings(resource_id, status)
    WHERE status = 'APPROVED';
