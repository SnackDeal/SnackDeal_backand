package io.snackdeal.backand.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String impUid;
    private String merchantUid;

    private Long amount;
    private String payMethod;
    private String pgProvider;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String receiptUrl;

    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long orderId;

    @Builder
    public Payment(Long amount, String pgProvider, Long orderId, String merchantUid) {
        this.amount = amount;
        this.pgProvider = pgProvider;
        this.orderId = orderId;
        this.merchantUid = merchantUid;
        this.status = PaymentStatus.READY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 결제 검증 성공 시 포트원에서 조회한 결제 정보로 PAID 확정한다.
    public void markPaid(String impUid, String payMethod, String pgProvider,
                         String receiptUrl, LocalDateTime paidAt) {
        this.impUid = impUid;
        this.payMethod = payMethod;
        this.pgProvider = pgProvider;
        this.receiptUrl = receiptUrl;
        this.paidAt = paidAt;
        this.status = PaymentStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }

    // 금액 위변조/환불 등으로 결제를 취소 처리한다.
    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
