DROP TABLE IF EXISTS specimens;
DROP TABLE IF EXISTS products;
DROP SEQUENCE IF EXISTS products_seq;
DROP SEQUENCE IF EXISTS specimens_seq;

CREATE SEQUENCE products_seq AS INTEGER START WITH 100;
CREATE SEQUENCE specimens_seq AS INTEGER START WITH 100;

CREATE TABLE products (
    id              INTEGER PRIMARY KEY DEFAULT nextval('products_seq'),
    name            VARCHAR                 NOT NULL,
    type            VARCHAR                 NOT NULL,
    count           INTEGER,
    price           FLOAT                   NOT NULL,
    able_to_buy     BOOLEAN                 NOT NULL,
    created         TIMESTAMP DEFAULT now() NOT NULL
);

CREATE TABLE specimens (
    id              INTEGER PRIMARY KEY DEFAULT nextval('specimens_seq'),
    custom_id       VARCHAR                  NOT NULL,
    product_id      INTEGER                  NOT NULL,
    able_to_buy     BOOLEAN                  NOT NULL,
    created         TIMESTAMP DEFAULT now()  NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE (custom_id)
);