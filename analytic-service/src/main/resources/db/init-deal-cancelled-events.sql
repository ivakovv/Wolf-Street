CREATE TABLE IF NOT EXISTS cancelled_deals (
    id UUID DEFAULT generateUUIDv4(),
    order_id Int64 NOT NULL,
    portfolio_id Int64 NOT NULL,
    count Int64 NOT NULL,
    lot_price Decimal(18, 4) NOT NULL,
    instrument_id Int64 NOT NULL,
    order_type Enum8('BUY' = 0, 'SALE' = 1) NOT NULL,
    created_at DateTime NOT NULL
)
ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM(created_at)
ORDER BY (portfolio_id, order_id, created_at)