package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 결제 검증 요청(포트원 V2).
 * paymentId 는 prepare 에서 발급한 주문번호와 동일하며, 프론트가 결제창에 넘긴 값 그대로 되돌려준다.
 */
@Schema(description = "결제 검증 요청")
public record OrderCompleteRequest(
        @Schema(description = "포트원 결제창에 사용한 paymentId (= prepare 응답의 paymentId = 주문번호)",
                example = "ORD-20260705-00123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String paymentId
) {
}
