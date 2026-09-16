CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    status VARCHAR(40) NOT NULL,
    carrier VARCHAR(80),
    tracking_number VARCHAR(120),
    estimated_delivery DATE,
    total_amount NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO orders
(order_number, customer_name, status, carrier, tracking_number, estimated_delivery, total_amount)
VALUES
('10001', 'Demo Customer', 'SHIPPED', 'DHL', 'DHL-DEMO-10001', CURRENT_DATE + 2, 129.99),
('10002', 'Demo Customer', 'PROCESSING', NULL, NULL, CURRENT_DATE + 5, 89.50),
('10003', 'Demo Customer', 'DELIVERED', 'FedEx', 'FDX-DEMO-10003', CURRENT_DATE - 1, 249.00),
('10004', 'Demo Customer', 'OUT_FOR_DELIVERY', 'UPS', 'UPS-DEMO-10004', CURRENT_DATE, 59.99)
ON CONFLICT (order_number) DO NOTHING;
