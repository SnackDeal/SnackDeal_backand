CREATE TABLE order_item (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(50)  NOT NULL,
    price        BIGINT       NOT NULL,
    quantity     INTEGER      NOT NULL,
    product_id   BIGINT       NOT NULL,
    order_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
