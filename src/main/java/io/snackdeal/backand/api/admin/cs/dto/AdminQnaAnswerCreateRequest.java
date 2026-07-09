package io.snackdeal.backand.api.admin.cs.dto;

import jakarta.validation.constraints.NotBlank;

/** 관리자 QNA 답변 등록 요청 */
public record AdminQnaAnswerCreateRequest(
        @NotBlank String content
) {
}