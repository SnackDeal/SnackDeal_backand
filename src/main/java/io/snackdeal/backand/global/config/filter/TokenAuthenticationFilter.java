package io.snackdeal.backand.global.config.filter;

import io.snackdeal.backand.domain.member.service.JwtTokenProvider;
import io.snackdeal.backand.domain.member.service.MemberService;
import io.snackdeal.backand.domain.member.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validate(token)) {

            Map<String, Object> payload = jwtTokenProvider.getClaims(token);

            String email = payload.get("email").toString();
            Object sidObj = payload.get("sid");

            if (sidObj != null) {
                String sid = sidObj.toString();
                String activeSid = refreshTokenService.getActiveSessionId(email).orElse(null);
                if (activeSid == null || !activeSid.equals(sid)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            } else {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = memberService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
