package io.snackdeal.backand.api.user.cs.dto;

import java.time.LocalDateTime;

/** 공지사항 목록 항목 응답. */
public record NoticeSummaryResponse(
        Long id,
        String title,
        boolean pinned,
        LocalDateTime createdAt
) {
}
