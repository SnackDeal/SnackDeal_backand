package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.constraints.NotBlank;

/** 결제 검증 요청. 포트원에서 받은 imp_uid 와 prepare 에서 발급한 merchant_uid 를 전달한다. */
public record OrderCompleteRequest(
        @NotBlank String impUid,
        @NotBlank String merchantUid
) {
}
