ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(255);

UPDATE users
SET name = CASE
    WHEN email = 'koffy.doggy@gmail.com' THEN 'Koffy Doggy'
    WHEN user_id IS NOT NULL THEN INITCAP(REPLACE(REPLACE(REPLACE(SPLIT_PART(email, '@', 1), '.', ' '), '_', ' '), '-', ' '))
    ELSE 'Unknown User'
END
WHERE name IS NULL OR name = '';

ALTER TABLE users ALTER COLUMN name SET NOT NULL;