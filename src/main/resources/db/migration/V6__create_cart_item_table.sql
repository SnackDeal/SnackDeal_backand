CREATE TABLE cart_item (
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    quantity   INTEGER  NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    member_id  BIGINT   NOT NULL,
    product_id BIGINT   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cart_item_member_product UNIQUE (member_id, product_id),
    CONSTRAINT fk_cart_item_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
