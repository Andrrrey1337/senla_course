INSERT INTO room (id, number, capacity, stars, price, status, check_in_date, check_out_date)
VALUES
(1, 101, 2, 3, 3500.00, 'AVAILABLE', NULL, NULL),
(2, 102, 1, 4, 5000.00, 'OCCUPIED', '2025-01-01', '2025-01-05');

INSERT INTO guest (id, name)
VALUES
(1, 'Ivan'),
(2, 'Petr');

INSERT INTO residence (id, guest_id, room_id, check_in_date, check_out_date)
VALUES
(1, 1, 2, '2025-01-01', '2025-01-05'),
(2, 2, 1, '2024-12-20', '2024-12-25');

INSERT INTO service (id, name, price)
VALUES
(1, 'breakfast', 500.00),
(2, 'SPA', 3000.00);

INSERT INTO serviceRecord (id, guest_id, service_id, date)
VALUES
(1, 1, 1, '2025-01-02'),
(2, 1, 2, '2025-01-03');
