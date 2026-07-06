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

        @Schema(description = "비밀번호(영문+숫자+특수문자 8~16자)", example = "p@ssW0rd!")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상 16자 이하여야 합니다."
        ) String password, // @NotBlank 제거: 소셜 로그인 시 비밀번호는 백엔드에서 생성

        @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "생년월일", example = "2000-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull LocalDate birth,

        @Schema(description = "성별", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Gender gender,

        @Schema(description = "휴대폰번호(하이픈 포함/미포함 모두 허용)", example = "010-1111-2222", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Pattern(regexp = "^0\\d{1,2}-?\\d{3,4}-?\\d{4}$", message = "올바른 휴대폰번호 형식이 아닙니다.") String phone,

        // 이메일 인증(verify-code) 성공 시 발급된 토큰 (소셜 로그인 시 불필요)
        @Schema(description = "이메일 인증 토큰(verify-code 응답값)", example = "evt_a1b2c3d4")
        String verificationToken,

        @Schema(description = "소셜 로그인 여부 (true 이면 비밀번호 자동 생성)", example = "false", defaultValue = "false")
        Boolean isSocialLogin // boolean -> Boolean 으로 변경
) {
        public boolean socialLogin() {
                return isSocialLogin != null && isSocialLogin;
        }
}
