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
                  IssueType issueType, Long couponBoardId) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderPrice = (minOrderPrice != null) ? minOrderPrice : 0L;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0;
        this.issueType = issueType;
        this.isActive = true;
        this.couponBoardId = couponBoardId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseIssuedQuantity() {
        this.issuedQuantity += 1;
    }
}
