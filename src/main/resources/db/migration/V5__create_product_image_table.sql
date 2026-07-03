CREATE TABLE product_image (
    id             BIGINT  NOT NULL AUTO_INCREMENT,
    attachment_url TEXT    NOT NULL,
    sort_order     INTEGER NOT NULL,
    product_id     BIGINT  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
