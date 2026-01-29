CREATE TABLE order_item (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_price NUMERIC(14,2) NOT NULL,
    quantity NUMERIC(14,2) NOT NULL,

    CONSTRAINT pk_order_item PRIMARY KEY (id),
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES "order"(id),
    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id) REFERENCES product(id)
);

create index idx_order_item_order_id on order_item(order_id);
create index idx_order_item_product_id on order_item(product_id);
