package io.snackdeal.backand.global.config.handler;

import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        MemberDetails details = (MemberDetails) authentication.getPrincipal();
        TokenResponse tokens = authService.issueTokens(details.getEmail(), details.getRole());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"accessToken\":\"" + tokens.accessToken() + "\",\"refreshToken\":\"" + tokens.refreshToken() + "\"}"
        );
    }
}
