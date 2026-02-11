-- Fix user table schema for email-based authentication
-- This migration handles the transition from username to name field

-- Add name column if it doesn't exist
SET @name_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                    WHERE TABLE_SCHEMA = DATABASE() 
                    AND TABLE_NAME = 'users' 
                    AND COLUMN_NAME = 'name');

SET @sql = IF(@name_exists = 0, 
              'ALTER TABLE users ADD COLUMN name VARCHAR(100) NULL AFTER id', 
              'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migrate existing data from username to name if needed
SET @username_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'users' 
                        AND COLUMN_NAME = 'username');

SET @sql = IF(@username_exists > 0, 
              'UPDATE users SET name = COALESCE(name, username) WHERE name IS NULL OR name = \'\'', 
              'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Set default name for any remaining null values
UPDATE users SET name = 'User' WHERE name IS NULL OR name = '';

-- Make name column NOT NULL
ALTER TABLE users MODIFY COLUMN name VARCHAR(100) NOT NULL;

-- Drop username column if it exists
SET @sql = IF(@username_exists > 0, 
              'ALTER TABLE users DROP COLUMN username', 
              'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update admin user data
UPDATE users SET name = 'System Administrator', email = 'admin@company.com' 
WHERE email = 'admin@local' OR email = 'admin@company.com';

-- Add performance indexes (MySQL compatible)
SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
                     WHERE TABLE_SCHEMA = DATABASE() 
                     AND TABLE_NAME = 'users' 
                     AND INDEX_NAME = 'idx_users_email');

SET @sql = IF(@index_exists = 0, 
              'CREATE INDEX idx_users_email ON users(email)', 
              'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
                     WHERE TABLE_SCHEMA = DATABASE() 
                     AND TABLE_NAME = 'users' 
                     AND INDEX_NAME = 'idx_users_active');

SET @sql = IF(@index_exists = 0, 
              'CREATE INDEX idx_users_active ON users(is_active)', 
              'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;