package io.snackdeal.backand.global.mail;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 공용 메일 모듈 설정.
 * JavaMailSender 는 spring.mail.* 값으로 스프링부트가 자동 구성하므로,
 * 여기서는 공용 MailProperties 바인딩만 활성화
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {
}
