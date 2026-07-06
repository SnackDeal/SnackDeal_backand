package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 사용자 환불 요청 */
@Schema(description = "환불 요청")
public record RefundRequest(
        @Schema(description = "환불 사유", example = "단순 변심", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String reason
) {
}
