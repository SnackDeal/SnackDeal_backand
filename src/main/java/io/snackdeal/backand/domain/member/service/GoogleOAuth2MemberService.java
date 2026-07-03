package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * member 테이블에 provider_id 컬럼이 없어(스키마 고정) 이메일 기준으로만 매칭한다.
 * 최초 가입 시 birth/gender/phone은 스키마상 NOT NULL이라 임시값으로 채우며,
 * 프론트에서 최초 로그인 후 추가 정보 입력 화면으로 보완하는 것을 전제로 한다.
 */
@Service
@RequiredArgsConstructor
public class GoogleOAuth2MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final LocalDate PLACEHOLDER_BIRTH = LocalDate.of(2000, 1, 1);

    private final MemberRepository repository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"google".equals(registrationId)) {
            return oauth2User;
        }

        Map<String, Object> attributes = new LinkedHashMap<>(oauth2User.getAttributes());
        String email = attributes.get("email").toString();
        String name = attributes.getOrDefault("name", email).toString();

        Member member = repository.findByEmail(email)
                .orElseGet(() -> repository.save(Member.builder()
                        .email(email)
                        .password(UUID.randomUUID().toString())
                        .name(name)
                        .birth(PLACEHOLDER_BIRTH)
                        .gender(Gender.MALE)
                        .phone("")
                        .role(MemberRole.USER)
                        .build()));

        attributes.put("email", member.getEmail());
        attributes.put("role", member.getRole().name());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name())),
                attributes,
                "email"
        );
    }
}
