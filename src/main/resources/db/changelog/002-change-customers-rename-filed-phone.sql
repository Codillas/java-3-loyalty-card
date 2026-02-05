--liquibase formatted sql

--changeset nikita.mykhailov:2

ALTER TABLE customers RENAME COLUMN phone TO phone_number;

