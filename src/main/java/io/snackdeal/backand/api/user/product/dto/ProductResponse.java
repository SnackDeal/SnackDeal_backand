package io.snackdeal.backand.api.user.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        Long price,
        String description,
        String imageUrl,
        Integer stock,
        ProductStatus status,
        Boolean isSoldout,
        Long categoryId,
        String category
) {
}
