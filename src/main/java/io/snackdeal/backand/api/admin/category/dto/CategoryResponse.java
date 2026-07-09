package io.snackdeal.backand.api.admin.category.dto;

import io.snackdeal.backand.domain.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "관리자 카테고리 응답")
public record CategoryResponse(
        @Schema(description = "카테고리 ID", example = "1")
        Long id,

        @Schema(description = "카테고리명", example = "과자")
        String name,

        @Schema(description = "정렬 순서", example = "1")
        Integer sortOrder,

        @Schema(description = "생성일시")
        LocalDateTime createdAt,

        @Schema(description = "수정일시")
        LocalDateTime updatedAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}