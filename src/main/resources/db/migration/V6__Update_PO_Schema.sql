-- Add missing columns to purchase_order table
ALTER TABLE purchase_order 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add missing columns to purchase_order_item table if it exists
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255),
    quantity INT,
    unit_price DECIMAL(19,2),
    tax DECIMAL(19,2),
    discount DECIMAL(19,2),
    item_subtotal DECIMAL(19,2),
    item_total DECIMAL(19,2),
    status ENUM('PENDING', 'DELIVERED') NOT NULL DEFAULT 'PENDING',
    purchase_order_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id)
);

-- Update existing status values
UPDATE purchase_order SET status = 'OPEN' WHERE status NOT IN ('OPEN', 'CLOSED');