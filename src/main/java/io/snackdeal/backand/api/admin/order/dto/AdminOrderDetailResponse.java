package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.snackdeal.backand.domain.order.entity.ShippingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 주문 상세.
 * 사용자용 상세와 달리 스케줄러 상태, imp_uid, 사용 쿠폰 상세 등 관리 정보를 함께 반환한다.
 * 스케줄러 미도입 상태이므로 scheduledNextStatus/scheduledNextAt 은 항상 null 로 내려간다.
 */
@Schema(description = "관리자 주문 상세")
public record AdminOrderDetailResponse(
        @Schema(description = "주문 ID", example = "123") Long id,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED") OrderStatus status,
        @Schema(description = "주문 일시", example = "2026-07-05T14:32:00") LocalDateTime orderedAt,
        @Schema(description = "결제 완료 일시", example = "2026-07-05T14:32:00") LocalDateTime paidAt,
        @Schema(description = "취소/환불 완료 일시 (해당 없으면 null)", example = "null") LocalDateTime cancelledAt,
        @Schema(description = "스케줄러 예정 다음 상태 (미도입, 항상 null)") OrderStatus scheduledNextStatus,
        @Schema(description = "스케줄러 예정 전환 일시 (미도입, 항상 null)") LocalDateTime scheduledNextAt,
        @Schema(description = "관리자 수동 변경 여부", example = "false") boolean manualOverride,
        @Schema(description = "구매자 정보") Buyer buyer,
        @Schema(description = "주문 상품 목록") List<Item> items,
        @Schema(description = "배송지 및 배송 상태") Shipping shipping,
        @Schema(description = "결제 정보") Payment payment
) {
    @Schema(description = "구매자 정보")
    public record Buyer(
            @Schema(description = "회원 ID", example = "45") Long id,
            @Schema(description = "이메일", example = "buyer@test.com") String email,
            @Schema(description = "이름", example = "홍길동") String name,
            @Schema(description = "총 주문 수", example = "7") long totalOrderCount
    ) {
    }

    @Schema(description = "주문 상품 항목")
    public record Item(
            @Schema(description = "상품 ID", example = "1") Long productId,
            @Schema(description = "상품명 (주문 시점 스냅샷)", example = "허니버터 프레첼") String productName,
            @Schema(description = "단가", example = "4500") Long price,
            @Schema(description = "수량", example = "2") Integer quantity,
            @Schema(description = "항목 합계 (price * quantity)", example = "9000") Long lineTotal
    ) {
    }

    @Schema(description = "배송지 및 배송 상태")
    public record Shipping(
            @Schema(description = "수령인 이름", example = "홍길동") String receiverName,
            @Schema(description = "수령인 휴대폰번호", example = "01012345678") String receiverPhone,
            @Schema(description = "우편번호", example = "06133") String zipcode,
            @Schema(description = "주소", example = "서울 강남구 테헤란로 123") String address,
            @Schema(description = "상세 주소", example = "456호") String detailAddress,
            @Schema(description = "배송 요청사항", example = "부재 시 문 앞") String deliveryRequest,
            @Schema(description = "택배사", example = "CJ대한통운") String courier,
            @Schema(description = "송장번호", example = "1234567890123") String trackingNumber,
            @Schema(description = "배송 상태 (READY/PREPARING/SHIPPING/DELIVERED)", example = "READY") ShippingStatus status
    ) {
    }

    @Schema(description = "결제 정보 (관리자용 — 사용 쿠폰 상세 포함)")
    public record Payment(
            @Schema(description = "상품 총액", example = "9000") Long productAmount,
            @Schema(description = "배송비", example = "0") Long shippingFee,
            @Schema(description = "사용 쿠폰 정보 (미사용 시 null)") UsedCoupon usedCoupon,
            @Schema(description = "할인 금액", example = "0") Long discountAmount,
            @Schema(description = "최종 결제 금액", example = "9000") Long finalAmount,
            @Schema(description = "결제수단", example = "Card") String payMethod,
            @Schema(description = "PG사", example = "TOSSPAYMENTS") String pgProvider,
            @Schema(description = "결제 상태 (READY/PAID/FAILED/CANCELLED)", example = "PAID") PaymentStatus status,
            @Schema(description = "포트원 paymentId (= 주문번호)", example = "ORD-20260705-00123") String paymentId
    ) {
    }

    @Schema(description = "사용 쿠폰 정보")
    public record UsedCoupon(
            @Schema(description = "사용자 쿠폰 ID", example = "3") Long userCouponId,
            @Schema(description = "쿠폰명", example = "신규가입 10% 할인") String couponName,
            @Schema(description = "할인 유형 (PERCENT / FIXED)", example = "PERCENT") DiscountType discountType,
            @Schema(description = "할인 값 (PERCENT: %, FIXED: 원)", example = "10") Long discountValue
    ) {
    }
}
