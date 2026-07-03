package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.member.service.EmailVerificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository repository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("join - TODO")
    void join_Success() {
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
    @DisplayName("findDescriptionByEmail - TODO")
    void findDescriptionByEmail_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("updateProfile - TODO")
    void updateProfile_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findAll - TODO")
    void findAll_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findById - TODO")
    void findById_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("changeStatus - TODO")
    void changeStatus_Success() {
        fail("not implemented");
    }

}