package org.jh.oauthjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OauthJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthJwtApplication.class, args);
    }

}
