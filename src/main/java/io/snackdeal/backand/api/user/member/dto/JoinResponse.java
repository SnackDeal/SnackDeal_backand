package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답 소셜 회원가입인 경우 accessToken/refreshToken 이 함께 발급되어 별도 로그인 없이 바로 세션을 시작할 수 있다.")
public record JoinResponse(
        @Schema(description = "생성된 회원 정보") MemberDescription member,

        @Schema(description = "AccessToken (소셜 회원가입 시에만 발급, 일반 회원가입 시 null)")
        String accessToken,

        @Schema(description = "RefreshToken (소셜 회원가입 시에만 발급, 일반 회원가입 시 null)")
        String refreshToken
) {
    public static JoinResponse of(MemberDescription member) {
        return new JoinResponse(member, null, null);
    }

    public static JoinResponse of(MemberDescription member, TokenResponse tokens) {
        return new JoinResponse(member, tokens.accessToken(), tokens.refreshToken());
    }
}