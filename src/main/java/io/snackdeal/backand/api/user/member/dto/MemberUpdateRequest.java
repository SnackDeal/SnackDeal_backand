package io.snackdeal.backand.api.user.member.dto;

import jakarta.validation.constraints.Pattern;

public record MemberUpdateRequest(
        String phone,
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상 16자 이하여야 합니다."
        ) String password
) {
}
