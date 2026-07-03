CREATE TABLE qna_answer (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    content     TEXT     NOT NULL,
    answered_at DATETIME NOT NULL,
    qna_id      BIGINT   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_qna_answer_qna UNIQUE (qna_id),
    CONSTRAINT fk_qna_answer_qna FOREIGN KEY (qna_id) REFERENCES qna (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
