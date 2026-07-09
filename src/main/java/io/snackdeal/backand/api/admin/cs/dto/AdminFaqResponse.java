package io.snackdeal.backand.api.admin.cs.dto;

import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;

import java.time.LocalDateTime;

public record AdminFaqResponse(
        Long id,
        QnaType type,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminFaqResponse from(Faq faq) {
        return new AdminFaqResponse(
                faq.getId(),
                faq.getType(),
                faq.getTitle(),
                faq.getContent(),
                faq.getCreatedAt(),
                faq.getUpdatedAt()
        );
    }
}