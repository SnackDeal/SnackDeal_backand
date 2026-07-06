package io.snackdeal.backand.api.user.cs.dto;

import jakarta.validation.constraints.NotBlank;

/** 1:1 문의 수정 요청 */
public record QnaUpdateRequest(
        @NotBlank String title,
        @NotBlank String content,
        String attachmentUrl
) {
}
