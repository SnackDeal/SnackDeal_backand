CREATE TABLE user_coupon (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    status     VARCHAR(50) NOT NULL,
    issued_at  DATETIME    NOT NULL,
    used_at    DATETIME    NULL,
    member_id  BIGINT      NOT NULL,
    coupon_id  BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_coupon_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_user_coupon_coupon FOREIGN KEY (coupon_id) REFERENCES coupon (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
