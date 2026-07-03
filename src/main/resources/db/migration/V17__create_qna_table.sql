CREATE TABLE qna (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    type           VARCHAR(50) NOT NULL,
    title          VARCHAR(50) NOT NULL,
    content        TEXT        NOT NULL,
    attachment_url TEXT        NULL,
    is_answered    TINYINT(1)  NOT NULL DEFAULT 0,
    created_at     DATETIME    NOT NULL,
    updated_at     DATETIME    NULL,
    deleted_at     DATETIME    NULL,
    member_id      BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_qna_member FOREIGN KEY (member_id) REFERENCES member (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
