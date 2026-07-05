package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.snackdeal.backand.domain.order.entity.ShippingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 주문 상세.
 * 사용자용 상세와 달리 스케줄러 상태, imp_uid, 사용 쿠폰 상세 등 관리 정보를 함께 반환한다.
 * 스케줄러 미도입 상태이므로 scheduledNextStatus/scheduledNextAt 은 항상 null 로 내려간다.
 */
public record AdminOrderDetailResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        LocalDateTime orderedAt,
        LocalDateTime paidAt,
        LocalDateTime cancelledAt,
        OrderStatus scheduledNextStatus,
        LocalDateTime scheduledNextAt,
        boolean manualOverride,
        Buyer buyer,
        List<Item> items,
        Shipping shipping,
        Payment payment
) {
    public record Buyer(Long id, String email, String name, long totalOrderCount) {
    }

    public record Item(Long productId, String productName, Long price, Integer quantity, Long lineTotal) {
    }

    public record Shipping(
            String receiverName, String receiverPhone, String zipcode, String address,
            String detailAddress, String deliveryRequest, String courier, String trackingNumber,
            ShippingStatus status) {
    }

    public record Payment(
            Long productAmount, Long shippingFee, UsedCoupon usedCoupon, Long discountAmount,
            Long finalAmount, String payMethod, String pgProvider, PaymentStatus status, String impUid) {
    }

    public record UsedCoupon(Long userCouponId, String couponName, DiscountType discountType, Long discountValue) {
    }
}
