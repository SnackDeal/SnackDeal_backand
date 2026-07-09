package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.ShippingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/** 주문 상세의 배송지/배송상태 응답 */
@Schema(description = "배송지 및 배송 상태")
public record ShippingResponse(
        @Schema(description = "수령인 이름", example = "홍길동") String receiverName,
        @Schema(description = "수령인 휴대폰번호", example = "01012345678") String receiverPhone,
        @Schema(description = "우편번호", example = "06133") String zipcode,
        @Schema(description = "주소", example = "서울 강남구 테헤란로 123") String address,
        @Schema(description = "상세 주소", example = "456호") String detailAddress,
        @Schema(description = "배송 요청사항", example = "부재 시 문 앞") String deliveryRequest,
        @Schema(description = "택배사", example = "CJ대한통운") String courier,
        @Schema(description = "송장번호", example = "1234567890123") String trackingNumber,
        @Schema(description = "배송 상태 (READY/PREPARING/SHIPPING/DELIVERED)", example = "READY") ShippingStatus status
) {
}
