CREATE TABLE product_image (
    id UUID NOT NULL,
    product_id UUID NOT NULL,
    object_key VARCHAR(512) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255),
    size BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_product_image
        PRIMARY KEY (id),

    CONSTRAINT uq_product_image_object_key
        UNIQUE (object_key),

    CONSTRAINT fk_product_image_product
        FOREIGN KEY (product_id)
        REFERENCES product (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_product_image_product_id
    ON product_image (product_id);
