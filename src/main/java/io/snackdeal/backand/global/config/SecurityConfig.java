package io.snackdeal.backand.global.config;

import io.snackdeal.backand.global.config.filter.TokenAuthenticationFilter;
import io.snackdeal.backand.global.config.handler.AuthenticationEntryPointImpl;
import io.snackdeal.backand.global.config.handler.OAuth2SuccessHandler;
import io.snackdeal.backand.domain.member.service.GoogleOAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final GoogleOAuth2MemberService googleOAuth2MemberService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Qualifier("authenticationSuccessHandlerImpl") AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository
    ) throws Exception {
        HttpSecurity security = http
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .formLogin(f -> f
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/", "/login", "/signup").permitAll()

                        .requestMatchers(HttpMethod.POST, "/member/join", "/member/email/**", "/member/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/member/token/refresh").permitAll()
                        .requestMatchers("/member/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cs/notice/**", "/cs/qna/faq").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cs/qna/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/cs/qna", "/chatbot/ask").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/cs/qna/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/cs/qna/**").authenticated()

                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers("/order/**").authenticated()
                        .requestMatchers("/delivery/**").authenticated()
                        .requestMatchers("/mypage/**").authenticated()
                        .requestMatchers("/event/coupon/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (clientRegistrationRepository.getIfAvailable() != null) {
            security.oauth2Login(oauth -> oauth
                    .userInfoEndpoint(ui -> ui.userService(googleOAuth2MemberService))
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(failureHandler)
            );
        }

        return security.build();
    }
}
