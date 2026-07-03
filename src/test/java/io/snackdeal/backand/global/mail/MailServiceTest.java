package io.snackdeal.backand.global.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("send - TODO")
    void send_Success() {
        fail("not implemented");
    }

}