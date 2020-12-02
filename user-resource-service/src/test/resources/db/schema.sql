DROP TABLE IF EXISTS users_role;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS users_roles_seq;
DROP SEQUENCE IF EXISTS users_seq;

CREATE SEQUENCE users_seq AS INTEGER START WITH 100;
CREATE SEQUENCE users_roles_seq AS INTEGER START WITH 100;

CREATE TABLE users (
    id               INTEGER PRIMARY KEY DEFAULT nextval('users_seq'),
    name             VARCHAR                        NOT NULL,
    surname          VARCHAR                        NOT NULL,
    username         VARCHAR                        NOT NULL,
    password         VARCHAR                        NOT NULL,
    registered       TIMESTAMP DEFAULT now()        NOT NULL,
    UNIQUE (username)
);

CREATE TABLE users_role (
    id              INTEGER PRIMARY KEY DEFAULT nextval('users_roles_seq'),
    user_id         INTEGER                         NOT NULL,
    role            VARCHAR                         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (user_id, role)
);