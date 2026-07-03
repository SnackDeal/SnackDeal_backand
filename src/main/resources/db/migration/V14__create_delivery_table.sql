CREATE TABLE delivery (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(50)  NOT NULL,
    receiver_name   VARCHAR(50)  NOT NULL,
    receiver_phone  VARCHAR(20)  NOT NULL,
    zipcode         VARCHAR(10)  NOT NULL,
    address         VARCHAR(255) NOT NULL,
    detail_address  VARCHAR(255) NULL,
    is_default      TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NULL,
    deleted_at      DATETIME     NULL,
    member_id       BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_delivery_member FOREIGN KEY (member_id) REFERENCES member (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
