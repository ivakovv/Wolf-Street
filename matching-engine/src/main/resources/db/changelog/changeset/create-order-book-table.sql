CREATE TABLE order_book(
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    portfolio_id BIGINT NOT NULL,
    instrument_id BIGINT NOT NULL,
    count BIGINT NOT NULL,
    lot_price DECIMAL(18,2) NOT NULL,
    order_type VARCHAR(100) NOT NULL,
    order_status VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);