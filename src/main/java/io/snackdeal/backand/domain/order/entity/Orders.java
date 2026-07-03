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
}
