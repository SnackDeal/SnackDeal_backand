package io.snackdeal.backand.api.user.cs.dto;

import io.snackdeal.backand.domain.cs.entity.Notice;

import java.time.LocalDateTime;

/** 공지사항 목록 항목 응답 */
public record NoticeSummaryResponse(
        Long id,
        String title,
        boolean pinned,
        LocalDateTime createdAt
) {
    public static NoticeSummaryResponse from(Notice notice) {
        return new NoticeSummaryResponse(
                notice.getId(),
                notice.getTitle(),
                notice.isPinned(),
                notice.getCreatedAt()
        );
    }
}
