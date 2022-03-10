CREATE TYPE registrar.statuses AS ENUM
    ('new', 'completed', 'rejected');

ALTER TABLE registrar.users
    ADD COLUMN status registrar.statuses DEFAULT 'new';