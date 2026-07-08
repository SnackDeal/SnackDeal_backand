package io.snackdeal.backand.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Long discountValue;
    private Long minOrderPrice;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private Integer totalQuantity;
    private Integer issuedQuantity;

    @Enumerated(EnumType.STRING)
    private IssueType issueType;

    private boolean isActive;

    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Long couponBoardId;

    @Builder
    public Coupon(String name, DiscountType discountType, Long discountValue, Long minOrderPrice,
                  LocalDateTime validFrom, LocalDateTime validUntil, Integer totalQuantity,
                  IssueType issueType, Long couponBoardId, Boolean isActive) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderPrice = (minOrderPrice != null) ? minOrderPrice : 0L;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.totalQuantity = normalizeTotalQuantity(totalQuantity);
        this.issuedQuantity = 0;
        this.issueType = issueType;
        this.isActive = isActive == null || isActive;
        this.couponBoardId = couponBoardId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasLimitedQuantity() {
        return totalQuantity != null && totalQuantity > 0;
    }

    public void increaseIssuedQuantity() {
        if (hasLimitedQuantity() && issuedQuantity >= totalQuantity) {
            throw new BusinessException(ResponseCode.COUPON_SOLD_OUT);
        }
        this.issuedQuantity += 1;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeActive(boolean active) {
        this.isActive = active;
        this.updatedAt = LocalDateTime.now();
    }

    public CouponStatus deriveStatus(LocalDateTime now) {
        if (Boolean.FALSE.equals(isActive)) {
            return CouponStatus.STOPPED;
        }
        if (validUntil != null && now.isAfter(validUntil)) {
            return CouponStatus.EXPIRED;
        }
        return CouponStatus.ACTIVE;
    }

    private Integer normalizeTotalQuantity(Integer totalQuantity) {
        if (totalQuantity == null || totalQuantity <= 0) {
            return 0;
        }
        return totalQuantity;
    }
}
