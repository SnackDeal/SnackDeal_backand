package io.snackdeal.backand.api.user.cs.dto;

import io.snackdeal.backand.domain.cs.entity.QnaType;

import java.time.LocalDateTime;

/** 1:1 문의 상세 응답(답변 포함) */
public record QnaResponse(
        Long id,
        QnaType type,
        String title,
        String content,
        String attachmentUrl,
        boolean answered,
        LocalDateTime createdAt,
        String answerContent,
        LocalDateTime answeredAt
) {
}
