-- V10: Official Timetable Entries (recurring weekly class schedule)
CREATE TABLE IF NOT EXISTS timetable_entries (
    id          BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    created_by  INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_timetable_resource_day
    ON timetable_entries(resource_id, day_of_week);
