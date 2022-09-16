--liquibase formatted sql

--changeset user:orders
CREATE TABLE IF NOT EXISTS orders(
    "id"            BIGINT      PRIMARY KEY DEFAULT 1,
    "movie_id"      BIGINT      REFERENCES movies("id") ,
    "order_time"    Timestamp   NOT NULL DEFAULT(now()),
    "participants"  INT         NOT NULL,
    CHECK (participants> 0)
);
