# Database Schema Design

## Core Tables

### users
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_users_email (email),
    INDEX idx_users_active (is_active)
);
```

### roles
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);
```

### user_roles (Join Table)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
```

### vendors
```sql
CREATE TABLE vendors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    userName VARCHAR(150),
    email VARCHAR(150),
    phone VARCHAR(50),
    address VARCHAR(255),
    gst_number VARCHAR(50),
    rating DECIMAL(3,2),
    location VARCHAR(100),
    category VARCHAR(100),
    compliance BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vendors_rating (rating),
    INDEX idx_vendors_location (location),
    INDEX idx_vendors_category (category),
    INDEX idx_vendors_compliance (compliance)
);
```

## Workflow Tables

### purchase_requisition
```sql
CREATE TABLE purchase_requisition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pr_number VARCHAR(50) UNIQUE,
    requester_id BIGINT NOT NULL,
    description TEXT,
    quantity INTEGER,
    unit_price DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    status ENUM('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(id),
    INDEX idx_pr_status (status),
    INDEX idx_pr_requester (requester_id),
    INDEX idx_pr_created_at (created_at)
);
```

### approval_history
```sql
CREATE TABLE approval_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requisition_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL, -- 'APPROVED', 'REJECTED', 'SUBMITTED'
    comment TEXT,
    action_by VARCHAR(150) NOT NULL,
    action_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requisition_id) REFERENCES purchase_requisition(id) ON DELETE CASCADE,
    INDEX idx_approval_history_requisition (requisition_id),
    INDEX idx_approval_history_action_at (action_at)
);
```

## Purchase Order Tables

### purchase_order
```sql
CREATE TABLE purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    subtotal DECIMAL(15,2),
    tax DECIMAL(15,2),
    discount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    status ENUM('OPEN', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_purchase_order_status (status),
    INDEX idx_purchase_order_created_at (created_at)
);
```

### purchase_order_item
```sql
CREATE TABLE purchase_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    item_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    tax DECIMAL(15,2) DEFAULT 0,
    discount DECIMAL(15,2) DEFAULT 0,
    item_subtotal DECIMAL(15,2),
    item_total DECIMAL(15,2),
    status ENUM('PENDING', 'PARTIALLY_DELIVERED', 'DELIVERED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id) ON DELETE CASCADE,
    INDEX idx_po_item_status (status),
    INDEX idx_po_item_po_id (purchase_order_id)
);
```

## Default Data

### Roles
```sql
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System Administrator'),
('PROCUREMENT', 'Procurement Team Member'),
('FINANCE', 'Finance Team Member'),
('VENDOR', 'Vendor User');
```

### Default Admin User
```sql
INSERT INTO users (name, email, password, is_active) VALUES
('System Administrator', 'admin@company.com', '$2a$10$...', TRUE);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1); -- Assign ADMIN role to first user
```

## Indexes for Performance

### Search Optimization
- `idx_vendors_rating`: Optimizes vendor search by rating
- `idx_vendors_location`: Optimizes vendor search by location
- `idx_vendors_category`: Optimizes vendor search by category
- `idx_vendors_compliance`: Optimizes vendor search by compliance status

### Authentication & Authorization
- `idx_users_email`: Optimizes login queries
- `idx_users_active`: Optimizes active user queries

### Workflow Optimization
- `idx_pr_status`: Optimizes requisition status queries
- `idx_approval_history_requisition`: Optimizes approval history retrieval
- `idx_approval_history_action_at`: Optimizes chronological queries

### Purchase Order Optimization
- `idx_purchase_order_status`: Optimizes PO status queries
- `idx_po_item_status`: Optimizes item delivery status queries
- `idx_po_item_po_id`: Optimizes item-to-PO relationship queries

## Data Relationships

1. **Users ↔ Roles**: Many-to-Many relationship through `user_roles`
2. **Users → Purchase Requisitions**: One-to-Many (requester relationship)
3. **Purchase Requisitions → Approval History**: One-to-Many
4. **Purchase Orders → Purchase Order Items**: One-to-Many
5. **Vendors**: Standalone entity with search optimization

## Business Rules Enforced by Schema

1. **Email Uniqueness**: Users must have unique email addresses
2. **Role Assignment**: Users must have at least one role
3. **Workflow States**: Purchase requisitions follow DRAFT → SUBMITTED → APPROVED/REJECTED flow
4. **PO Auto-Close**: Purchase orders automatically close when all items are delivered
5. **Audit Trail**: All approval actions are recorded with timestamp and user
6. **Data Integrity**: Foreign key constraints ensure referential integrity