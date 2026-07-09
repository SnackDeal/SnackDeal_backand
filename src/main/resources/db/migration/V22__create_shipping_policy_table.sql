-- 배송비 정책(단일 행) 무료기준 20,000원 / 기본 배송비 0원으로 초기화 관리자 API 로 변경
CREATE TABLE shipping_policy (
    id             BIGINT   NOT NULL,
    base_fee       BIGINT   NOT NULL,
    free_threshold BIGINT   NOT NULL,
    updated_at     DATETIME NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO shipping_policy (id, base_fee, free_threshold, updated_at)
VALUES (1, 0, 20000, NOW());
