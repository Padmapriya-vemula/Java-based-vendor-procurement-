-- =========================
-- ROLES
-- =========================
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- USER_ROLES (Many-to-Many)
-- =========================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- =========================
-- VENDORS
-- =========================
CREATE TABLE vendors (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    username    VARCHAR(150),
    email       VARCHAR(150),
    phone       VARCHAR(50),
    address     VARCHAR(255),
    gst_number  VARCHAR(50),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- PURCHASE REQUISITIONS (PR)
-- =========================
CREATE TABLE purchase_requisitions (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    pr_number      VARCHAR(50) NOT NULL UNIQUE,
    requester_id   BIGINT      NOT NULL,
    vendor_id      BIGINT,
    status         VARCHAR(30) NOT NULL,
    total_amount   DECIMAL(15,2),
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pr_requester
        FOREIGN KEY (requester_id) REFERENCES users (id),

    CONSTRAINT fk_pr_vendor
        FOREIGN KEY (vendor_id) REFERENCES vendors (id)
);

-- =========================
-- PURCHASE ORDERS (PO)
-- =========================
CREATE TABLE purchase_orders (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    po_number      VARCHAR(50) NOT NULL UNIQUE,

    pr_id          BIGINT      NOT NULL,
    vendor_id      BIGINT      NOT NULL,

    status         VARCHAR(30) NOT NULL,
    total_amount   DECIMAL(15,2),

    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_po_pr
        FOREIGN KEY (pr_id) REFERENCES purchase_requisitions (id),

    CONSTRAINT fk_po_vendor
        FOREIGN KEY (vendor_id) REFERENCES vendors (id)
);
