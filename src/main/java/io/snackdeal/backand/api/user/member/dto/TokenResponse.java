package io.snackdeal.backand.api.user.member.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
