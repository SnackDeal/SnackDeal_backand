CREATE TABLE category (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    sort_order INTEGER     NOT NULL,
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NULL,
    deleted_at DATETIME    NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_category_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
