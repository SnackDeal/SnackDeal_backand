package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.domain.member.entity.Member;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * member 테이블에 provider_id 컬럼이 없어(스키마 고정) 이메일 기준으로만 매칭
 * 신규 이메일이면 여기서 회원을 만들지 않고 isNewUser=true 만 표시
 * 실제 가입(및 birth/gender/phone 입력)은 프론트 회원가입 화면 → /member/join 에서 완료
 */
@Service
@RequiredArgsConstructor
public class GoogleOAuth2MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

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

        Optional<Member> member = repository.findByEmail(email);

        attributes.put("email", email);
        attributes.put("name", name);
        attributes.put("isNewUser", member.isEmpty());
        attributes.put("role", member.map(m -> m.getRole().name()).orElse("USER"));

        // DefaultOAuth2User는 authorities가 비어있으면 예외를 던지므로, 신규 유저에게는 임시로 GUEST 권한을 부여
        List<SimpleGrantedAuthority> authorities = member.isEmpty()
                ? List.of(new SimpleGrantedAuthority("ROLE_GUEST"))
                : List.of(new SimpleGrantedAuthority("ROLE_" + member.get().getRole().name()));

        return new DefaultOAuth2User(authorities, attributes, "email");
    }
}
