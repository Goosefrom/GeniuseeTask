--liquibase formatted sql

--changeset user:movies
CREATE TABLE IF NOT EXISTS movies(
    "id"               BIGINT PRIMARY KEY   DEFAULT 1,
    "name"             VARCHAR(255)       NOT NULL UNIQUE,
    "release_date"     Timestamp          NOT NULL ,
    "cost"             INT                NOT NULL DEFAULT 0,
    CHECK (cost> 0)
);
