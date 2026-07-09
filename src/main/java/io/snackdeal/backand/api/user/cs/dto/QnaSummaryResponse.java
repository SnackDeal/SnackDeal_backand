package io.snackdeal.backand.api.user.cs.dto;

import io.snackdeal.backand.domain.cs.entity.QnaType;

import java.time.LocalDateTime;

/** 1:1 문의 목록 항목 응답 */
public record QnaSummaryResponse(
        Long id,
        QnaType type,
        String title,
        boolean answered,
        LocalDateTime createdAt
) {
}
