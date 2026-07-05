package io.snackdeal.backand.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자 주문/결제 API(Swagger) 문서용 합성 어노테이션 모음.
 */
public interface OrderApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "주문/결제", description = "주문 준비 · 결제 검증 · 주문내역 · 환불 요청 API (로그인 필요)")
    @interface Doc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 준비",
            description = "재고를 확인(차감 없음)하고 주문을 PENDING_PAYMENT 로 임시 생성한다.\n\n"
                    + "응답의 `paymentId`(=주문번호)와 `amount`를 포트원 V2 결제창(`requestPayment`)에 그대로 전달한다.\n"
                    + "`storeId`/`channelKey`도 함께 내려주므로 프론트에서 하드코딩 불필요.\n\n"
                    + "배송지는 `deliveryId`(주소록) 또는 `shipping` 직접 입력 중 하나 필수.\n"
                    + "쿠폰 사용 시 `userCouponId` 전달 — 본인/ACTIVE/유효기간/최소주문금액 검증.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "준비 성공 (paymentId, amount, storeId, channelKey, 구매자 정보)"),
            @ApiResponse(responseCode = "400", description = "필수 입력값 누락 (배송지 없음 등)"),
            @ApiResponse(responseCode = "404", description = "상품 없음(PR001) / 배송지 없음(DL001)"),
            @ApiResponse(responseCode = "409", description = "쿠폰 조건 미달(CO005)"),
            @ApiResponse(responseCode = "422", description = "재고 부족(OR005)")
    })
    @interface Prepare {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "결제 검증 및 주문 확정",
            description = "포트원 V2에서 실제 결제금액을 조회해 DB 예정금액과 대조한다.\n\n"
                    + "- 일치 + PAID: 재고 차감 · 쿠폰 USED · 주문 PAYMENT_COMPLETED · 결제 PAID (트랜잭션 원자처리)\n"
                    + "- 금액 불일치: 포트원 결제 자동 취소 후 422(OR006)\n"
                    + "- 미결제(READY 등): 포트원 결제 자동 취소 후 422(OR007)\n\n"
                    + "`paymentId`는 prepare 응답의 값(=주문번호)을 그대로 전달한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "확정 성공 (주문/결제 상세)"),
            @ApiResponse(responseCode = "403", description = "타인의 주문(OR009)"),
            @ApiResponse(responseCode = "404", description = "주문 없음(OR001)"),
            @ApiResponse(responseCode = "422", description = "금액 불일치→자동취소(OR006) / 미결제→자동취소(OR007) / 이미 처리된 주문(OR012)"),
            @ApiResponse(responseCode = "502", description = "포트원 검증 실패(OR008)")
    })
    @interface Complete {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "내 주문내역",
            description = "로그인한 사용자의 주문 목록을 최신순으로 페이징 조회한다.\n\n"
                    + "각 항목에 대표 상품명(`mainProductName`)과 상품 종류 수(`itemCount`)를 포함한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근")
    })
    @interface List {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 상세",
            description = "주문 상품 내역 · 배송지 · 결제 정보를 반환한다.\n\n본인 주문만 조회 가능하며, 타인 접근 시 403.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "타인의 주문(OR009)"),
            @ApiResponse(responseCode = "404", description = "주문 없음(OR001)")
    })
    @interface FindById {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "환불 요청",
            description = "결제완료(`PAYMENT_COMPLETED`) 또는 배송준비중(`PREPARING_SHIPMENT`) 상태에서만 요청 가능.\n\n"
                    + "주문 상태가 `REFUND_REQUESTED`로 변경되며, 실제 승인/거절은 관리자 환불처리 API에서 이뤄진다.\n"
                    + "배송중/배송완료 등은 고객센터 문의 대상(422).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 요청 접수 (status=REFUND_REQUESTED)"),
            @ApiResponse(responseCode = "403", description = "타인의 주문(OR009)"),
            @ApiResponse(responseCode = "404", description = "주문 없음(OR001)"),
            @ApiResponse(responseCode = "422", description = "환불 불가 상태(OR010)")
    })
    @interface Refund {
    }
}
