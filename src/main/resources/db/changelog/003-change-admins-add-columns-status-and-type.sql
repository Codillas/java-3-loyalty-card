--liquibase formatted sql

--changeset nikita.mykhailov:3

ALTER TABLE admins RENAME COLUMN role TO type;

ALTER TABLE admins ADD COLUMN status TEXT;
