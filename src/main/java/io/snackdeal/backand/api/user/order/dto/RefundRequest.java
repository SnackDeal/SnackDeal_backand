package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.constraints.NotBlank;

/** 사용자 환불 요청. */
public record RefundRequest(
        @NotBlank String reason
) {
}
