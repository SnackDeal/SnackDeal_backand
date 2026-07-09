package io.snackdeal.backand.global.config.handler;

import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${custom.front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Boolean isNewUser = oAuth2User.getAttribute("isNewUser");

        if (Boolean.TRUE.equals(isNewUser)) {
            String name = oAuth2User.getAttribute("name");
            response.sendRedirect(frontUrl + "/signup"
                    + "?email=" + encode(email)
                    + "&name=" + encode(name));
            return;
        }

        String role = oAuth2User.getAttribute("role");
        TokenResponse tokens = authService.issueTokens(email, MemberRole.valueOf(role));

        response.sendRedirect(frontUrl + "/oauth2/callback"
                + "?accessToken=" + encode(tokens.accessToken())
                + "&refreshToken=" + encode(tokens.refreshToken()));
    }

    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
}
