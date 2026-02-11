-- Update user table to use name instead of username
-- Handle existing data gracefully

-- Check if username column exists and migrate data
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'users' 
                   AND COLUMN_NAME = 'username');

-- Add name column if it doesn't exist
SET @name_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                    WHERE TABLE_SCHEMA = DATABASE() 
                    AND TABLE_NAME = 'users' 
                    AND COLUMN_NAME = 'name');

SET @sql = IF(@name_exists = 0, 
              'ALTER TABLE users ADD COLUMN name VARCHAR(100) NULL AFTER id', 
              'SELECT "Name column already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migrate data from username to name if username exists
SET @sql = IF(@col_exists > 0, 
              'UPDATE users SET name = COALESCE(name, username) WHERE name IS NULL OR name = ""', 
              'SELECT "Username column does not exist"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Make name column NOT NULL after data migration
ALTER TABLE users MODIFY COLUMN name VARCHAR(100) NOT NULL;

-- Drop username column if it exists
SET @sql = IF(@col_exists > 0, 
              'ALTER TABLE users DROP COLUMN username', 
              'SELECT "Username column already dropped"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing admin user
UPDATE users SET name = 'System Administrator' WHERE email = 'admin@local';
UPDATE users SET email = 'admin@company.com' WHERE email = 'admin@local';

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);

-- Add indexes for vendor search optimization
CREATE INDEX IF NOT EXISTS idx_vendors_rating ON vendors(rating);
CREATE INDEX IF NOT EXISTS idx_vendors_location ON vendors(location);
CREATE INDEX IF NOT EXISTS idx_vendors_category ON vendors(category);
CREATE INDEX IF NOT EXISTS idx_vendors_compliance ON vendors(compliance);

-- Add indexes for approval history
CREATE INDEX IF NOT EXISTS idx_approval_history_requisition ON approval_history(requisition_id);
CREATE INDEX IF NOT EXISTS idx_approval_history_action_at ON approval_history(action_at);

-- Add indexes for purchase orders
CREATE INDEX IF NOT EXISTS idx_purchase_order_status ON purchase_order(status);
CREATE INDEX IF NOT EXISTS idx_purchase_order_created_at ON purchase_order(created_at);

-- Add indexes for purchase order items
CREATE INDEX IF NOT EXISTS idx_po_item_status ON purchase_order_item(status);
CREATE INDEX IF NOT EXISTS idx_po_item_po_id ON purchase_order_item(purchase_order_id);