package io.snackdeal.backand.global.config.handler;

import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String role = oAuth2User.getAttribute("role");

        TokenResponse tokens = authService.issueTokens(email, MemberRole.valueOf(role));

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"accessToken\":\"" + tokens.accessToken() + "\",\"refreshToken\":\"" + tokens.refreshToken() + "\"}"
        );
    }
}
