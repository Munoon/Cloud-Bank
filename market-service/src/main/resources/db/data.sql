DELETE FROM products;
DELETE FROM specimens;
ALTER SEQUENCE products_seq RESTART WITH 100;
ALTER SEQUENCE specimens_seq RESTART WITH 100;

INSERT INTO products (name, type, count, price, able_to_buy) VALUES
    ('Snickers', 'FIXED_SPECIMEN', NULL, 10.0, true),
    ('Twix', 'UNFIXED_SPECIMEN', 10, 15.0, true),
    ('Bounty', 'UNFIXED_SPECIMEN', 20, 20.0, false);

INSERT INTO specimens (custom_id, product_id, able_to_buy) VALUES
    ('snickers_1', 100, true),
    ('snickers_2', 100, false)