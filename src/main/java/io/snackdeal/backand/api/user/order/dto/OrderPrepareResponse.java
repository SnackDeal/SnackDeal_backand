package io.snackdeal.backand.api.user.order.dto;

/**
 * 주문 준비 응답. 프론트가 이 값으로 포트원 결제창을 띄운다.
 * merchantUid(=주문번호)와 확정된 결제 예정 금액(amount), 구매자 정보를 내려준다.
 */
public record OrderPrepareResponse(
        String merchantUid,
        Long amount,
        String buyerEmail,
        String buyerName,
        String buyerTel
) {
}
