package io.snackdeal.backand.global.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MailProperties mailProperties;

    @Test
    @DisplayName("send - 발신자/수신자/제목/본문을 담아 메일을 전송")
    void send_Success() {
        when(mailProperties.from()).thenReturn("noreply@snackdeal.io");

        mailService.send("user@test.com", "제목", "본문");

        // 실제로 mailSender 에 넘어간 메시지 내용을 캡처해서 검증
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertEquals("noreply@snackdeal.io", sent.getFrom());
        assertArrayEquals(new String[]{"user@test.com"}, sent.getTo());
        assertEquals("제목", sent.getSubject());
        assertEquals("본문", sent.getText());
    }
}
