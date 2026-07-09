package io.snackdeal.backand.api.admin.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "카테고리 순서 변경 요청")
public record CategoryOrderRequest(
        @Schema(description = "카테고리 순서 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "카테고리 순서 목록은 비어 있을 수 없습니다.")
        @Valid
        List<CategoryOrderItem> categoryOrders
) {

    @Schema(description = "카테고리 순서 항목")
    public record CategoryOrderItem(
            @Schema(description = "카테고리 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "카테고리 ID는 필수입니다.")
            Long categoryId,

            @Schema(description = "정렬 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "정렬 순서는 필수입니다.")
            @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
            Integer sortOrder
    ) {
    }
}
