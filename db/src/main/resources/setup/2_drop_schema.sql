DROP SCHEMA IF EXISTS registrar CASCADE;

DO
$$
    BEGIN
        IF EXISTS(SELECT FROM pg_roles WHERE rolname = 'registrar') THEN
            EXECUTE 'REASSIGN OWNED BY customers TO postgres;';
            EXECUTE 'DROP OWNED BY registrar;';
        END IF;
    END
$$;


DROP USER IF EXISTS registrar;