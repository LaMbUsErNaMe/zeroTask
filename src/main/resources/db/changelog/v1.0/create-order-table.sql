CREATE TABLE "order" (
    id UUID NOT NULL,
    customer_id BIGINT NOT NULL,
    status VARCHAR(99) NOT NULL,
    delivery_address VARCHAR(99) NOT NULL,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

ALTER TABLE "order"
ADD CONSTRAINT fk_order_customer
FOREIGN KEY (customer_id) REFERENCES customer(id);

create index idx_order_customer_id on "order"(customer_id);
