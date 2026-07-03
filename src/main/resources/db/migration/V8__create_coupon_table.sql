CREATE TABLE coupon (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    name             VARCHAR(50) NOT NULL,
    discount_type    VARCHAR(50) NOT NULL,
    discount_value   BIGINT      NOT NULL,
    min_order_price  BIGINT      NOT NULL DEFAULT 0,
    valid_from       DATETIME    NOT NULL,
    valid_until      DATETIME    NULL,
    total_quantity   INTEGER     NOT NULL,
    issued_quantity  INTEGER     NOT NULL DEFAULT 0,
    issue_type       VARCHAR(50) NOT NULL,
    is_active        TINYINT(1)  NOT NULL DEFAULT 1,
    deleted_at       DATETIME    NULL,
    updated_at       DATETIME    NULL,
    created_at       DATETIME    NOT NULL,
    coupon_board_id  BIGINT      NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_coupon_coupon_board FOREIGN KEY (coupon_board_id) REFERENCES coupon_board (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
