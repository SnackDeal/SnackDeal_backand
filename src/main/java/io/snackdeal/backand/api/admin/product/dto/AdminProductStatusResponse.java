package io.snackdeal.backand.api.admin.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record AdminProductStatusResponse(
        Long id,
        ProductStatus status,
        LocalDateTime updatedAt
) {
}
