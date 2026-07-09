package io.snackdeal.backand.api.user.cs.dto;

import io.snackdeal.backand.domain.cs.entity.Notice;

import java.time.LocalDateTime;

/** 공지사항 상세 응답 */
public record NoticeResponse(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.isPinned(),
                notice.getCreatedAt(),
                notice.getDeletedAt()
        );
    }
}
