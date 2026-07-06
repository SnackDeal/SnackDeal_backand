package io.snackdeal.backand.api.user.cs.dto;

import io.snackdeal.backand.domain.cs.entity.QnaType;

/** FAQ 응답 */
public record FaqResponse(
        Long id,
        QnaType type,
        String title,
        String content
) {
}
