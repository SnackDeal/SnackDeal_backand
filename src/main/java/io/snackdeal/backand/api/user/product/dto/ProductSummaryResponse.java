package io.snackdeal.backand.api.user.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;

/** 상품 목록 항목 응답 */
public record ProductSummaryResponse(
        Long id,
        String name,
        Long price,
        String thumbnailUrl,
        Long categoryId,
        String category,
        Boolean isSoldout
) {
}
