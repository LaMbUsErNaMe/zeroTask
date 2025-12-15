CREATE SCHEMA IF NOT EXISTS product_schema;-- CHARACTER SET=utf8 COLLATE=utf8_bin;

SET search_path TO product_schema;

CREATE TABLE products (
    id UUID NOT NULL,
    name VARCHAR(99) NOT NULL,
    product_number BIGINT NOT NULL,
    description TEXT,
    category_type VARCHAR(99) NOT NULL,
    price NUMERIC(14, 2) NOT NULL,
    quantity NUMERIC(14, 2) NOT NULL,
    quantity_changed_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    created_date DATE NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT unique_products_product_number UNIQUE (product_number)
);

create index idx_products_name on products(name);

create index idx_products_product_number on products(product_number);

INSERT INTO "product_schema"."products" (id, name, product_number, category_type, price, quantity, quantity_changed_date_time, created_date)
SELECT
    gen_random_uuid(),
    'Phone model №' || gs,
    gs,
    'SMARTPHONES',
    100000,
    100,
	'2025-12-14 15:33:09.114785+04',
	'2025-12-14'
FROM generate_series(1, 1000000) gs;