-- Ensure Module C ticketing tables exist for databases that recorded older V12 without these tables.

CREATE TABLE IF NOT EXISTS incident_tickets (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    resource_location VARCHAR(500),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    reporter_email VARCHAR(255) NOT NULL,
    assigned_technician_email VARCHAR(255),
    rejection_reason TEXT,
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ticket_comments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    commenter_email VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES incident_tickets(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ticket_attachments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    data_url TEXT NOT NULL,
    file_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES incident_tickets(id) ON DELETE CASCADE
);

DROP TRIGGER IF EXISTS update_incident_tickets_timestamp ON incident_tickets;
CREATE TRIGGER update_incident_tickets_timestamp
BEFORE UPDATE ON incident_tickets
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

DROP TRIGGER IF EXISTS update_ticket_comments_timestamp ON ticket_comments;
CREATE TRIGGER update_ticket_comments_timestamp
BEFORE UPDATE ON ticket_comments
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();
