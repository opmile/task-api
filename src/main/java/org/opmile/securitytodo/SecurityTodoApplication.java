package org.opmile.securitytodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SecurityTodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityTodoApplication.class, args);
    }

}
