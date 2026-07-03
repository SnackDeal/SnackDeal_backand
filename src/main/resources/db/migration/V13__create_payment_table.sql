CREATE TABLE payment (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    imp_uid      VARCHAR(255) NULL,
    merchant_uid VARCHAR(255) NULL,
    amount       BIGINT       NOT NULL,
    pay_method   VARCHAR(50)  NULL,
    pg_provider  VARCHAR(50)  NULL,
    status       VARCHAR(50)  NOT NULL,
    receipt_url  VARCHAR(255) NULL,
    paid_at      DATETIME     NULL,
    cancelled_at DATETIME     NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NULL,
    order_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_payment_order UNIQUE (order_id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
