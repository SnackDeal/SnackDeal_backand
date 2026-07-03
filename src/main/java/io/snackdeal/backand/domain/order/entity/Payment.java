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
    public Payment(Long amount, String pgProvider, Long orderId) {
        this.amount = amount;
        this.pgProvider = pgProvider;
        this.orderId = orderId;
        this.status = PaymentStatus.READY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
