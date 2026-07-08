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
 * 공개 배송비 정책 조회 API(Swagger) 문서용 합성 어노테이션 모음.
 */
public interface ShippingPolicyApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "배송비 정책", description = "배송비 무료기준 · 기본 배송비 조회 API (로그인 불필요, 상품상세 등 공개 화면용)")
    @interface Doc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송비 정책 조회",
            description = "현재 배송비 정책(무료기준 금액 · 기본 배송비)을 조회 (인증 불필요)\n\n"
                    + "정책 행이 없으면 기본값(무료기준 20,000원 / 배송비 0원)으로 자동 생성 후 반환\n\n"
                    + "배송비 계산: 상품총액 >= freeThreshold → 0원, 미만 → baseFee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @interface Get {
    }
}
