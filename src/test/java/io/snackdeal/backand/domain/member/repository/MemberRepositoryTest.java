package io.snackdeal.backand.domain.member.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("컨텍스트 로드 - TODO")
    void contextLoads() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findByEmail - TODO")
    void findByEmail_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findAll - TODO")
    void findAll_Success() {
        fail("not implemented");
    }

}