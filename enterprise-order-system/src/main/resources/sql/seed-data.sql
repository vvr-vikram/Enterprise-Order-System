-- ============================================
-- Enterprise Order Application - Seed Data
-- ============================================

USE enterprise_order_db;

-- Roles
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

-- Users
INSERT IGNORE INTO users (id, username, email, enabled) VALUES
('user-123', 'john_doe', 'john@example.com', true),
('user-456', 'jane_doe', 'jane@example.com', true);

-- User-Roles
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
('user-123', 2),
('user-456', 2);

-- Inventory (Products)
INSERT IGNORE INTO inventory (id, product_id, stock_count) VALUES
('inv-101', 'prod-101', 50),
('inv-102', 'prod-102', 10);
