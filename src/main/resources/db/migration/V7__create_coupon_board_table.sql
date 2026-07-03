CREATE TABLE coupon_board (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(100) NOT NULL,
    content       TEXT         NOT NULL,
    thumbnail_url VARCHAR(255) NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    start_at      DATETIME     NOT NULL,
    end_at        DATETIME     NULL,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NULL,
    deleted_at    DATETIME     NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
