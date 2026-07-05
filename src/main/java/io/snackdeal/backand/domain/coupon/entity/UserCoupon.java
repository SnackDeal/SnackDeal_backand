package io.snackdeal.backand.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;

    private Long memberId;
    private Long couponId;

    @Builder
    public UserCoupon(Long memberId, Long couponId) {
        this.memberId = memberId;
        this.couponId = couponId;
        this.status = UserCouponStatus.ACTIVE;
        this.issuedAt = LocalDateTime.now();
    }

    public void use() {
        this.status = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    // 주문 취소/환불로 사용을 되돌려 재사용 가능 상태로 복구한다.
    public void restore() {
        this.status = UserCouponStatus.ACTIVE;
        this.usedAt = null;
    }
}
