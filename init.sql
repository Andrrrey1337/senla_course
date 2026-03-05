CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    balance DECIMAL(19, 4) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE transfers (
    id UUID PRIMARY KEY,
    from_account_id BIGINT NOT NULL,
    to_account_id BIGINT NOT NULL,
    amount DECIMAL(19, 4) NOT NULL CHECK (amount > 0),
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_from_account FOREIGN KEY (from_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_to_account FOREIGN KEY (to_account_id) REFERENCES accounts(id)
);

CREATE INDEX idx_transfers_from_account ON transfers(from_account_id);
CREATE INDEX idx_transfers_to_account ON transfers(to_account_id);