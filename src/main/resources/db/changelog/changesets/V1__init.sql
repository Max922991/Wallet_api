CREATE TABLE IF NOT EXISTS wallet
(
    wallet_id     UUID   NOT NULL PRIMARY KEY,
    balance       BIGINT NOT NULL,
    transaction_Id UUID UNIQUE
);


