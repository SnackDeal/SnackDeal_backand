package io.snackdeal.backand.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 배송비 정책 (단일 행 설정, id=1 고정).
 * baseFee: 무료기준 미만일 때 부과할 배송비 freeThreshold: 이 금액 이상 주문 시 무료.
 * 관리자 API 로 변경할 수 있으며, 값이 없으면 서비스가 기본값(무료기준 20,000 / 배송비 0)으로 동작
 */
@Getter
@Entity
@Table(name = "shipping_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingPolicy {

    @Id
    private Long id;

    private Long baseFee;
    private Long freeThreshold;

    private LocalDateTime updatedAt;

    @Builder
    public ShippingPolicy(Long id, Long baseFee, Long freeThreshold) {
        this.id = id;
        this.baseFee = baseFee;
        this.freeThreshold = freeThreshold;
        this.updatedAt = LocalDateTime.now();
    }

    // 배송비/무료기준 변경 null 인 항목은 기존 값을 유지(부분 수정).
    public void update(Long baseFee, Long freeThreshold) {
        if (baseFee != null) {
            this.baseFee = baseFee;
        }
        if (freeThreshold != null) {
            this.freeThreshold = freeThreshold;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // 상품총액에 대한 배송비 계산: 무료기준 이상이면 0, 아니면 baseFee.
    public long calculateFee(long productAmount) {
        return (productAmount >= freeThreshold) ? 0L : baseFee;
    }
}
