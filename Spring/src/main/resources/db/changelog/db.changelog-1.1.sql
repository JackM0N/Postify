--liquibase formatted sql
--changeset Stanislaw:4 labels:schema,fix

ALTER TABLE user_role
    ADD COLUMN role_name varchar(50) NOT NULL DEFAULT 'USER';

UPDATE user_role
SET role_name = (
    SELECT r.role_name
    FROM role r
    WHERE r.role_id = user_role.role_id
);

ALTER TABLE user_role
    DROP COLUMN role_id;

DROP TABLE role;

