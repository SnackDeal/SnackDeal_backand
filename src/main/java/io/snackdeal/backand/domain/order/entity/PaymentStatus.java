package io.snackdeal.backand.domain.order.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 상태: READY(결제대기) / PAID(결제완료) / FAILED(결제실패) / CANCELLED(결제취소)")
public enum PaymentStatus {
    READY, PAID, FAILED, CANCELLED
}
