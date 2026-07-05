package io.snackdeal.backand.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    private Long productAmount;
    private Long shippingFee;
    private Long discountAmount;
    private Long finalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderedAt;
    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Long memberId;
    private Long userCouponId;

    // 관리자가 상태를 수동 변경했는지 여부. true 면 (추후 도입될) 스케줄러 자동 진행 대상에서 제외된다.
    private boolean manualOverride;

    // 환불 요청 직전 상태. 관리자가 환불을 거절하면 이 값으로 되돌린다.
    @Enumerated(EnumType.STRING)
    private OrderStatus previousStatus;

    @Builder
    public Orders(String orderNumber, Long productAmount, Long shippingFee, Long discountAmount,
                  Long finalAmount, Long memberId, Long userCouponId) {
        this.orderNumber = orderNumber;
        this.productAmount = productAmount;
        this.shippingFee = shippingFee;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.orderedAt = LocalDateTime.now();
        this.memberId = memberId;
        this.userCouponId = userCouponId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == OrderStatus.CANCELLED) {
            this.cancelledAt = LocalDateTime.now();
        }
    }

    // 결제 검증 성공 시 결제완료로 확정한다.
    public void markPaymentCompleted() {
        this.status = OrderStatus.PAYMENT_COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    // 관리자 수동 상태 변경. manualOverride 를 켜고, 취소 시 cancelledAt 을 기록한다.
    public void changeStatusByAdmin(OrderStatus status) {
        this.status = status;
        this.manualOverride = true;
        this.updatedAt = LocalDateTime.now();
        if (status == OrderStatus.CANCELLED) {
            this.cancelledAt = LocalDateTime.now();
        }
    }

    // 사용자 환불 요청. 되돌릴 수 있도록 직전 상태를 보관한다.
    public void requestRefund() {
        this.previousStatus = this.status;
        this.status = OrderStatus.REFUND_REQUESTED;
        this.updatedAt = LocalDateTime.now();
    }

    // 관리자 환불 승인. 환불완료 + 취소시각 기록.
    public void approveRefund() {
        this.status = OrderStatus.REFUND_COMPLETED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 관리자 환불 거절. 환불 요청 직전 상태로 되돌린다.
    public void rejectRefund() {
        this.status = (this.previousStatus != null) ? this.previousStatus : OrderStatus.PAYMENT_COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
}
