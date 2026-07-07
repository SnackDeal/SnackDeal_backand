package io.snackdeal.backand.api.admin.product.dto;

import io.snackdeal.backand.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record AdminProductStatusUpdateRequest(
        @NotNull
        ProductStatus status
) {
}
