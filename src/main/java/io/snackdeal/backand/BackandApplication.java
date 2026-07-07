package io.snackdeal.backand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BackandApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackandApplication.class, args);
    }

}
