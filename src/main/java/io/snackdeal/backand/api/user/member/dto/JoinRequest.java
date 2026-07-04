package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "회원가입 요청")
public record JoinRequest(
        @Schema(description = "이메일(로그인 아이디)", example = "test@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        @Schema(description = "비밀번호(영문+숫자+특수문자 8~16자)", example = "p@ssW0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상 16자 이하여야 합니다."
        ) String password,

        @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "생년월일", example = "2000-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull LocalDate birth,

        @Schema(description = "성별", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Gender gender,

        @Schema(description = "휴대폰번호(하이픈 없이)", example = "01011112222", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String phone,

        // 이메일 인증(verify-code) 성공 시 발급된 토큰
        @Schema(description = "이메일 인증 토큰(verify-code 응답값)", example = "evt_a1b2c3d4", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String verificationToken
) {
}
