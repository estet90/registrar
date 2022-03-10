CREATE TABLE registrar.users
(
    id                     BIGSERIAL    NOT NULL,
    first_name             VARCHAR(300) NOT NULL,
    last_name              VARCHAR(300) NOT NULL,
    middle_name            VARCHAR(300),
    passport               VARCHAR(20)  NOT NULL,
    birth_date             DATE         NOT NULL,
    passport_expiry_date   DATE         NOT NULL,
    citizenship            VARCHAR(300),
    email                  VARCHAR(300) NOT NULL,
    password               VARCHAR(300) NOT NULL,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT current_timestamp,
    completed_at           TIMESTAMPTZ,
    rejected_at            TIMESTAMPTZ,
    visa_request_date_from DATE,
    visa_request_date_to   DATE,
    visa_request_cities    VARCHAR(300)[],
    visa_response_status   INTEGER,
    visa_response_body     TEXT,

    CONSTRAINT users_pk PRIMARY KEY (id)
);