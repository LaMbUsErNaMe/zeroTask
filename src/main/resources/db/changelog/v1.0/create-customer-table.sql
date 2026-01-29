CREATE TABLE "customer" (
    id BIGINT NOT NULL,
    login VARCHAR(99) NOT NULL,
    email VARCHAR(99) NOT NULL,
    is_active BOOLEAN NOT NULL,

    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT unique_customer_login UNIQUE (login),
    CONSTRAINT unique_customer_email UNIQUE (email)
);

create index idx_product_name on product(name);

create index idx_product_product_number on product(product_number);

CREATE SEQUENCE customer_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE "customer"
    ALTER COLUMN id SET DEFAULT nextval('customer_seq');
