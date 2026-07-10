package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 주문 준비 응답. 프론트가 주문서 결제정보를 표시하고 포트원 결제창을 연다.
 */
@Schema(description = "주문 준비 응답")
public record OrderPrepareResponse(
        @Schema(description = "주문번호 (포트원 결제창 paymentId로 그대로 사용)", example = "ORD-20260705-00123")
        String paymentId,

        @Schema(description = "상품금액", example = "1400")
        Long productAmount,

        @Schema(description = "배송비", example = "0")
        Long shippingFee,

        @Schema(description = "할인금액", example = "0")
        Long discountAmount,

        @Schema(description = "결제 예정 금액 (상품금액 + 배송비 - 할인)", example = "1400")
        Long amount,

        @Schema(description = "포트원 Store ID (결제창 storeId 파라미터)", example = "store-abc123")
        String storeId,

        @Schema(description = "포트원 채널 키 (결제창 channelKey 파라미터)", example = "channel-key-abc")
        String channelKey,

        @Schema(description = "구매자 이메일", example = "buyer@test.com")
        String buyerEmail,

        @Schema(description = "구매자 이름", example = "홍길동")
        String buyerName,

        @Schema(description = "구매자 전화번호", example = "01011112222")
        String buyerTel
) {
}
