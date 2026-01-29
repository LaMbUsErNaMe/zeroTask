CREATE TABLE products (
    id UUID NOT NULL,
    name VARCHAR(99) NOT NULL,
    product_number BIGINT NOT NULL,
    description TEXT,
    category_type VARCHAR(99) NOT NULL,
    price NUMERIC(14, 2) NOT NULL,
    quantity NUMERIC(14, 2) NOT NULL,
    quantity_changed_date_time TIMESTAMP(6) NOT NULL,
    created_date DATE NOT NULL
);
