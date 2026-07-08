ALTER TABLE user_coupon
    ADD CONSTRAINT uk_user_coupon_member_coupon UNIQUE (member_id, coupon_id);

CREATE INDEX idx_coupon_issue_type_active_period
    ON coupon (issue_type, is_active, deleted_at, valid_from, valid_until);

CREATE INDEX idx_coupon_board_active_period
    ON coupon_board (is_active, deleted_at, start_at, end_at);

CREATE INDEX idx_user_coupon_member_status
    ON user_coupon (member_id, status);
