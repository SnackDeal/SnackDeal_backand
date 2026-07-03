CREATE TABLE faq (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    type       VARCHAR(50) NOT NULL,
    title      VARCHAR(50) NOT NULL,
    content    TEXT        NOT NULL,
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NULL,
    deleted_at DATETIME    NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
