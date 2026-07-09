package io.snackdeal.backand.api.user.cs.dto;

import io.snackdeal.backand.domain.cs.entity.QnaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 1:1 문의 수정 요청 */
public record QnaUpdateRequest(
        @NotNull QnaType type,
        @NotBlank @Size(max = 50) String title,
        @NotBlank String content,
        String attachmentUrl
) {
}
