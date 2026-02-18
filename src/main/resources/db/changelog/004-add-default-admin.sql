--liquibase formatted sql

--changeset nikita.mykhailov:4

-- Insert a default admin with a hardcoded UUID

INSERT INTO admins (id,
                    name,
                    email,
                    phone_number,
                    password,
                    created_at,
                    updated_at,
                    type,
                    status)
VALUES ('123e4567-e89b-12d3-a456-426614174000', -- Replace with any valid UUID if desired
        'Default Admin',
        'admin@admin.com',
        '+4912345678',
        '$2a$10$r.WkwIRHb1vP.cKi2J1L3uSP/69FtfzkzI/p.HH7KFUA./COSLhJK', -- 1234Qwerty!
        NOW(),
        NOW(),
        'SUPER_ADMIN',
        'ACTIVE');