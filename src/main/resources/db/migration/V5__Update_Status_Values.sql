-- Update existing status values to match new enum values
UPDATE purchase_order SET status = 'OPEN' WHERE status NOT IN ('OPEN', 'CLOSED');

-- Update PurchaseOrderItem status values if they exist
UPDATE purchase_order_item SET status = 'PENDING' WHERE status IS NULL OR status NOT IN ('PENDING', 'DELIVERED');