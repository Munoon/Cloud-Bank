DROP TABLE IF EXISTS products;
DROP SEQUENCE IF EXISTS products_seq;

CREATE SEQUENCE products_seq AS INTEGER START WITH 100;

CREATE TABLE products (
    id              INTEGER PRIMARY KEY DEFAULT nextval('products_seq'),
    name            VARCHAR                 NOT NULL,
    type            VARCHAR                 NOT NULL,
    count           INTEGER,
    price           FLOAT                   NOT NULL,
    able_to_buy     BOOLEAN                 NOT NULL,
    created         TIMESTAMP DEFAULT now() NOT NULL
);