package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증코드 발송 응답 expiresIn: 인증코드 유효시간(초).
 */
@Schema(description = "이메일 인증코드 발송 응답")
public record SendCodeResponse(
        @Schema(description = "인증코드 유효시간(초)", example = "300")
        int expiresIn
) {
}
