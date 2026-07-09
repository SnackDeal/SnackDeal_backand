package io.snackdeal.backand.api.admin.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminProductRequest(
        @NotBlank
        String name,

        @NotNull
        @Min(0)
        Long price,

        @NotNull
        Long categoryId,

        String description,

        @NotNull
        @Min(0)
        Integer stock,

        @NotBlank
        String imageUrl,

        @NotNull
        ProductStatus status
) {
}
