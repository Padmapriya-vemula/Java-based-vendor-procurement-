-- Add PARTIALLY_DELIVERED status support for purchase order items
-- This migration ensures the database accepts the new enum value

-- For PostgreSQL, no schema change needed as enum values are stored as strings
-- For MySQL, update any existing constraints if needed

-- Update any existing items with invalid status (optional cleanup)
-- No action needed as existing PENDING and DELIVERED values remain valid
