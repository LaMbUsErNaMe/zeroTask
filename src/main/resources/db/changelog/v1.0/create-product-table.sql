CREATE TABLE "product" (
    id UUID NOT NULL,
    name VARCHAR(99) NOT NULL,
    product_number BIGINT NOT NULL,
    description TEXT,
    category_type VARCHAR(99) NOT NULL,
    price NUMERIC(14, 2) NOT NULL,
    quantity NUMERIC(14, 2) NOT NULL,
    quantity_changed_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    created_date DATE NOT NULL,
    available BOOLEAN NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT unique_products_product_number UNIQUE (product_number)
);

INSERT INTO "public"."product" (id, name, product_number, category_type, price, quantity, quantity_changed_date_time, created_date, available)
SELECT
    gen_random_uuid(),
    'Phone model №' || gs,
    gs,
    'SMARTPHONES',
    100000,
    100,
	'2025-12-14 15:33:09.114785+04',
	'2025-12-14',
	true
FROM generate_series(1, 1000000) gs;