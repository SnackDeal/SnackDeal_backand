package io.snackdeal.backand.api.admin.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record AdminProductDetailResponse(
        Long id,
        String name,
        Long categoryId,
        String category,
        Long price,
        String description,
        Integer stock,
        ProductStatus status,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
