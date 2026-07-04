package io.snackdeal.backand.domain.member.repository;

import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member saveMember(String email, String name, MemberStatus status) {
        Member m = Member.builder()
                .email(email)
                .password("ENCODED")
                .name(name)
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .phone("01011112222")
                .role(MemberRole.USER)
                .build();
        if (status != MemberStatus.ACTIVE) {
            m.changeStatus(status);
        }
        return memberRepository.save(m);
    }

    @Test
    @DisplayName("existsByEmail - 가입된 이메일이면 true, 아니면 false")
    void existsByEmail() {
        saveMember("hong@test.com", "홍길동", MemberStatus.ACTIVE);

        assertTrue(memberRepository.existsByEmail("hong@test.com"));
        assertFalse(memberRepository.existsByEmail("none@test.com"));
    }

    @Test
    @DisplayName("countByCreatedAtBetween - 오늘 가입한 회원 수를 센다")
    void countByCreatedAtBetween() {
        saveMember("a@test.com", "회원A", MemberStatus.ACTIVE);
        saveMember("b@test.com", "회원B", MemberStatus.ACTIVE);

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        assertEquals(2, memberRepository.countByCreatedAtBetween(start, end));
    }

    @Test
    @DisplayName("search - 키워드(이메일/이름)로 부분검색된다")
    void search_ByKeyword() {
        saveMember("hong@test.com", "홍길동", MemberStatus.ACTIVE);
        saveMember("kim@test.com", "김철수", MemberStatus.ACTIVE);

        Page<Member> byEmail = memberRepository.search("hong", null, PageRequest.of(0, 10));
        Page<Member> byName = memberRepository.search("철수", null, PageRequest.of(0, 10));

        assertEquals(1, byEmail.getTotalElements());
        assertEquals("홍길동", byEmail.getContent().get(0).getName());
        assertEquals(1, byName.getTotalElements());
        assertEquals("kim@test.com", byName.getContent().get(0).getEmail());
    }

    @Test
    @DisplayName("search - status 필터로 걸러진다")
    void search_ByStatus() {
        saveMember("active@test.com", "활성", MemberStatus.ACTIVE);
        saveMember("inactive@test.com", "휴면", MemberStatus.INACTIVE);

        Page<Member> inactive = memberRepository.search(null, MemberStatus.INACTIVE, PageRequest.of(0, 10));

        assertEquals(1, inactive.getTotalElements());
        assertEquals("inactive@test.com", inactive.getContent().get(0).getEmail());
    }

    @Test
    @DisplayName("search - 조건이 모두 null 이면 전체 조회된다")
    void search_NoCondition() {
        saveMember("a@test.com", "회원A", MemberStatus.ACTIVE);
        saveMember("b@test.com", "회원B", MemberStatus.INACTIVE);

        Page<Member> all = memberRepository.search(null, null, PageRequest.of(0, 10));

        assertEquals(2, all.getTotalElements());
    }
}
