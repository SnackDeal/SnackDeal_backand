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
 * 관리자 주문관리 · 배송비 정책 API(Swagger) 문서용 합성 어노테이션 모음.
 */
public interface AdminOrderApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "관리자 - 주문관리", description = "주문 리스트/상세 조회 · 상태 변경 · 환불 처리 API (ROLE_ADMIN 필요)")
    @interface Doc {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "관리자 - 배송비 정책", description = "배송비 무료기준 · 기본 배송비 조회/변경 API (ROLE_ADMIN 필요)")
    @interface PolicyDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 리스트 조회",
            description = "전체 주문을 최신순으로 페이징 조회\n\n"
                    + "쿼리 파라미터(모두 선택):\n"
                    + "- `keyword`: 주문번호 또는 구매자 이메일/이름 부분 검색\n"
                    + "- `status`: 주문 상태 필터 (PENDING_PAYMENT / PAYMENT_COMPLETED / PREPARING_SHIPMENT / SHIPPED / COMPLETED / CANCELLED / REFUND_REQUESTED / REFUND_COMPLETED)\n"
                    + "- `dateFrom` / `dateTo`: 주문일 기간 필터 (yyyy-MM-dd)\n"
                    + "- `page` (기본 0) / `size` (기본 20)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @interface AdminOrderList {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 상세 조회 (관리자)",
            description = "사용자용 상세에 더해 구매자 정보(`buyer.totalOrderCount`) · 사용 쿠폰 상세(`payment.usedCoupon`) · "
                    + "`manualOverride` · `scheduledNextStatus`(스케줄러 미도입으로 항상 null) 등 관리 정보를 포함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 없음(OR001)")
    })
    @interface AdminOrderDetail {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 상태 변경",
            description = "지정 가능한 상태: `PREPARING_SHIPMENT` / `SHIPPED` / `COMPLETED` / `CANCELLED`\n\n"
                    + "변경 시 `manualOverride=true`로 설정된다(추후 스케줄러 자동 진행 제외 용도).\n\n"
                    + "**CANCELLED 전이 가능 상태**: PAYMENT_COMPLETED, PREPARING_SHIPMENT → 재고·쿠폰 자동 복구\n\n"
                    + "허용되지 않는 상태값(PENDING_PAYMENT 등) 지정 시 400(OR011), 잘못된 전이 시 422(OR012).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공 (manualOverride=true)"),
            @ApiResponse(responseCode = "400", description = "지정 불가 상태값(OR011)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 없음(OR001)"),
            @ApiResponse(responseCode = "422", description = "잘못된 상태 전이(OR012)")
    })
    @interface AdminOrderChangeStatus {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "환불 처리 (승인/거절)",
            description = "`REFUND_REQUESTED` 상태의 주문에 대해 승인 또는 거절을 처리\n\n"
                    + "**승인** (`approve: true`): 주문 REFUND_COMPLETED · 결제 CANCELLED · 재고 복구(`restoreStock` 기본 true) · "
                    + "쿠폰 복구(유효기간 남은 경우만)\n\n"
                    + "**거절** (`approve: false`): 환불 요청 직전 상태로 복귀 · `rejectReason` 필수\n\n"
                    + "쿠폰 복구 정책: `validUntil`이 남아 있으면 ACTIVE 복구, 만료됐으면 미복구.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "거절 사유 누락(OR014)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 없음(OR001)"),
            @ApiResponse(responseCode = "422", description = "환불요청 상태 아님(OR013)")
    })
    @interface AdminOrderRefund {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송비 정책 조회",
            description = "현재 배송비 정책(무료기준 금액 · 기본 배송비)을 조회\n\n"
                    + "정책 행이 없으면 기본값(무료기준 20,000원 / 배송비 0원)으로 자동 생성 후 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @interface ShippingPolicyGet {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송비 정책 변경",
            description = "`baseFee`(기본 배송비) · `freeThreshold`(무료기준 금액)를 부분 수정\n\n"
                    + "null인 항목은 기존 값을 유지 변경 즉시 신규 주문 prepare에 반영됨\n\n"
                    + "배송비 계산: 상품총액 >= freeThreshold → 0원, 미만 → baseFee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "음수 값 입력"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @interface ShippingPolicyUpdate {
    }
}
