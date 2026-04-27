-- Clean up failed migration
DROP TABLE IF EXISTS resource_notifications CASCADE;
DROP TABLE IF EXISTS resource_pricing CASCADE;
DROP TABLE IF EXISTS resource_inventory CASCADE;
DROP TABLE IF EXISTS resource_booking_schedules CASCADE;

-- Remove failed migration from flyway history
DELETE FROM flyway_schema_history WHERE version = '17';
