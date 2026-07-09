package io.snackdeal.backand.api.admin.cs.dto;

import io.snackdeal.backand.domain.cs.entity.QnaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminFaqRequest(
        @NotNull(message = "FAQ 유형은 필수입니다.")
        QnaType type,

        @NotBlank(message = "FAQ 제목은 필수입니다.")
        String title,

        @NotBlank(message = "FAQ 내용은 필수입니다.")
        String content
) {
}
