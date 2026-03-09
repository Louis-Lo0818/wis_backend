INSERT INTO product (code, name, weight) VALUES
('PRD-001', 'Industrial Bolt M10x50', 0.15),
('PRD-002', 'Steel Washer M10', 0.02),
('PRD-003', 'Hex Nut M10', 0.03),
('PRD-004', 'Copper Wire 2.5mm (100m)', 12.50),
('PRD-005', 'PVC Pipe 50mm (3m)', 3.80),
('PRD-006', 'LED Panel Light 60W', 2.10),
('PRD-007', 'Circuit Breaker 32A', 0.45),
('PRD-008', 'Rubber Gasket Set', 0.30)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO inventory (product_id, location, quantity) VALUES
(1, 'Warehouse-A', 500),
(1, 'Warehouse-B', 200),
(2, 'Warehouse-A', 1000),
(2, 'Warehouse-C', 750),
(3, 'Warehouse-A', 800),
(3, 'Warehouse-B', 350),
(4, 'Warehouse-B', 50),
(4, 'Warehouse-C', 30),
(5, 'Warehouse-A', 120),
(5, 'Warehouse-C', 60),
(6, 'Warehouse-B', 200),
(7, 'Warehouse-A', 150),
(7, 'Warehouse-C', 100),
(8, 'Warehouse-B', 400)
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);
