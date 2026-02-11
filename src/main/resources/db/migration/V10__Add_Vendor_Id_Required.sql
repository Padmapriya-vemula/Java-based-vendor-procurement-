-- Add vendor_id column to purchase_order table
ALTER TABLE purchase_order 
ADD COLUMN vendor_id BIGINT;

-- Make vendor_id required for purchase_requisitions
ALTER TABLE purchase_requisitions 
MODIFY COLUMN vendor_id BIGINT NOT NULL;
