--liquibase formatted sql

--changeset hotel_admin:1
CREATE TABLE guest (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE room (
    id BIGINT PRIMARY KEY,
    number INTEGER NOT NULL,
    capacity INTEGER NOT NULL,
    stars INTEGER NOT NULL,
    price NUMERIC(19,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    check_in_date DATE NULL,
    check_out_date DATE NULL,
    guest_id BIGINT,
    FOREIGN KEY (guest_id) REFERENCES guest(id)
);

CREATE TABLE service (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(19,2) NOT NULL
);

CREATE TABLE residence (
    id BIGINT PRIMARY KEY,
    guest_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    FOREIGN KEY (guest_id) REFERENCES Guest(id),
    FOREIGN KEY (room_id) REFERENCES Room(id)
);

CREATE TABLE serviceRecord (
    id BIGINT PRIMARY KEY,
    guest_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (guest_id) REFERENCES Guest(id),
    FOREIGN KEY (service_id) REFERENCES Service(id)
);