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
 * 사용자 장바구니 API(Swagger) 문서용 합성 어노테이션 모음.
 */
public interface CartApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "장바구니", description = "사용자 장바구니 조회 · 담기 · 수량 변경 · 삭제 API")
    @interface Doc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "장바구니 조회",
            description = "현재 로그인한 사용자의 장바구니 항목과 총 금액을 조회 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (CommonResponse<CartResponse>)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    })
    @interface FindCart {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "장바구니 담기",
            description = "동일 상품이 이미 담겨 있으면 수량을 합산하고, 없으면 새로 담는다 "
                    + "합산/신규 수량이 재고를 초과하면 거절 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "담기 성공 (CommonResponse<CartItemResponse>)"),
            @ApiResponse(responseCode = "400", description = "유효성 실패"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품"),
            @ApiResponse(responseCode = "422", description = "재고 부족")
    })
    @interface AddItem {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "장바구니 수량 변경",
            description = "요청한 수량으로 덮어쓴다 (cart_item.id 기준) 변경 수량이 재고를 초과하면 거절 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공 (CommonResponse<CartItemResponse>)"),
            @ApiResponse(responseCode = "400", description = "유효성 실패"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장바구니 항목 또는 상품"),
            @ApiResponse(responseCode = "422", description = "재고 부족")
    })
    @interface UpdateQuantity {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "장바구니 삭제",
            description = "요청 body가 없거나 cartItemIds가 비어있으면 장바구니 전체 삭제, "
                    + "값이 있으면 해당 항목만 삭제 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근")
    })
    @interface Delete {
    }
}
