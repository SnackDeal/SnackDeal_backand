CREATE TABLE member (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    status     VARCHAR(50)  NOT NULL,
    deleted_at DATETIME     NULL,
    updated_at DATETIME     NULL,
    created_at DATETIME     NOT NULL,
    birth      DATE         NOT NULL,
    gender     VARCHAR(50)  NOT NULL,
    last_login DATETIME     NULL,
    role       VARCHAR(20)  NOT NULL,
    phone      VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_member_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
