CREATE TABLE product (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    price       BIGINT       NOT NULL,
    description TEXT         NULL,
    status      VARCHAR(50)  NOT NULL,
    stock       INTEGER      NOT NULL,
    deleted_at  DATETIME     NULL,
    updated_at  DATETIME     NULL,
    created_at  DATETIME     NOT NULL,
    category_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
