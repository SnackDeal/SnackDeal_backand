package io.snackdeal.backand.api.admin.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;

public record AdminProductListResponse(
        Long id,
        String name,
        Long categoryId,
        String category,
        Long price,
        Integer stock,
        ProductStatus status,
        String thumbnailUrl
) {
}
