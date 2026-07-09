package io.snackdeal.backand.global.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 공용 메일 발송 설정 특정 도메인에 묶이지 않는 일반 값(발신자 주소 등)을 담는다.
 * 인증코드/토큰 만료시간 같은 도메인 전용 값은 각 도메인 설정에 둔다.
 */
@ConfigurationProperties(prefix = "custom.mail")
public record MailProperties(String from) {
}
