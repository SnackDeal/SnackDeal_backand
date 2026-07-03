package io.snackdeal.backand.api.user.cs.dto;

import java.time.LocalDateTime;

/** 공지사항 상세 응답. */
public record NoticeResponse(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime createdAt
) {
}
