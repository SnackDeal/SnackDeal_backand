CREATE TABLE email_verification (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    email               VARCHAR(50)  NOT NULL,
    code                VARCHAR(10)  NOT NULL,
    verification_token  VARCHAR(255) NULL,
    code_expires_at      DATETIME     NOT NULL,
    token_expires_at     DATETIME     NULL,
    verified            TINYINT(1)   NOT NULL DEFAULT 0,
    created_at          DATETIME     NOT NULL,
    member_id           BIGINT       NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_email_verification_member FOREIGN KEY (member_id) REFERENCES member (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
