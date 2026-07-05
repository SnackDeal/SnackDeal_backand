-- 관리자 수동 상태변경 플래그 + 환불 거절 시 되돌릴 직전 상태 컬럼 추가
ALTER TABLE orders ADD COLUMN manual_override BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE orders ADD COLUMN previous_status VARCHAR(50) NULL;
