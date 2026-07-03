package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.domain.member.repository.EmailVerificationRepository;
import io.snackdeal.backand.global.mail.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailVerificationRepository repository;
    @Mock
    private MailService mailService;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("sendCode - TODO")
    void sendCode_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("verifyCode - TODO")
    void verifyCode_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("getVerifiedEmail - TODO")
    void getVerifiedEmail_Success() {
        fail("not implemented");
    }

}