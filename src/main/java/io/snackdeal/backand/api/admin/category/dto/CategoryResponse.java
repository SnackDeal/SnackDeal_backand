package io.snackdeal.backand.api.admin.category.dto;

import io.snackdeal.backand.domain.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        Integer sortOrder,
        LocalDateTime createdAt,
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