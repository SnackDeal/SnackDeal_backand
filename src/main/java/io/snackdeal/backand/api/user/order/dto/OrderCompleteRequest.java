package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.constraints.NotBlank;

/** 결제 완료(검증) 요청. */
public record OrderCompleteRequest(
        @NotBlank String orderNumber,
        @NotBlank String impUid,
        String merchantUid
) {
}
