package io.snackdeal.backand.api.user.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

/** 상품 상세 응답 */
public record ProductResponse(
        Long id,
        String name,
        Long price,
        String description,
        ProductStatus status,
        Integer stock,
        Long categoryId,
        LocalDateTime createdAt
) {
}
