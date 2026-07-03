package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record JoinRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상 16자 이하여야 합니다."
        ) String password,
        @NotBlank String name,
        @NotNull LocalDate birth,
        @NotNull Gender gender,
        @NotBlank String phone,
        @NotBlank String verificationToken
) {
}
