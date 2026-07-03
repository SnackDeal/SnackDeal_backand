CREATE TABLE notice (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    title      VARCHAR(50) NOT NULL,
    content    TEXT        NOT NULL,
    is_pinned  TINYINT(1)  NOT NULL DEFAULT 0,
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NULL,
    deleted_at DATETIME    NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
