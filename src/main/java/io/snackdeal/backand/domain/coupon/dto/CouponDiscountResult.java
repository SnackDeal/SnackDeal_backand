package io.snackdeal.backand.domain.coupon.dto;

// 주문 쿠폰 할인 계산 결과
// finalAmount는 배송비 포함 결제금액이 아니라 상품 총액 기준 할인 후 금액
public record CouponDiscountResult(
        Long userCouponId,
        Long couponId,
        String couponName,
        Long discountAmount,
        Long finalAmount
) {

    public static CouponDiscountResult none(Long orderAmount) {
        return new CouponDiscountResult(null, null, null, 0L, orderAmount);
    }
}
