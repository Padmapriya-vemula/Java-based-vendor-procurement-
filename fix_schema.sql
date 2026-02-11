-- Fix the database schema manually
-- First, check if username column exists and drop it
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'spvms_db' 
     AND TABLE_NAME = 'users' 
     AND COLUMN_NAME = 'username') > 0,
    'ALTER TABLE users DROP COLUMN username',
    'SELECT "Username column does not exist"'
));

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Ensure name column exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'spvms_db' 
     AND TABLE_NAME = 'users' 
     AND COLUMN_NAME = 'name') = 0,
    'ALTER TABLE users ADD COLUMN name VARCHAR(100) NOT NULL AFTER id',
    'SELECT "Name column already exists"'
));

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing admin user if exists
UPDATE users SET name = 'System Administrator' WHERE email = 'admin@local';
UPDATE users SET email = 'admin@company.com' WHERE email = 'admin@local';

-- Add indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_vendors_rating ON vendors(rating);
CREATE INDEX IF NOT EXISTS idx_vendors_location ON vendors(location);
CREATE INDEX IF NOT EXISTS idx_vendors_category ON vendors(category);
CREATE INDEX IF NOT EXISTS idx_vendors_compliance ON vendors(compliance);