package io.snackdeal.backand.api.user.cs.dto;

import jakarta.validation.constraints.NotBlank;

/** 챗봇 질의 요청 */
public record ChatbotRequest(
        @NotBlank String question
) {
}
