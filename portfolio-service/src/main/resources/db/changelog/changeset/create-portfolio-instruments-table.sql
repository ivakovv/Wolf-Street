CREATE TABLE portfolio_instruments (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    portfolio_id BIGINT NOT NULL REFERENCES portfolio(id),
    instrument_id BIGINT NOT NULL,
    available_amount BIGINT NOT NULL DEFAULT 0,
    blocked_amount BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);