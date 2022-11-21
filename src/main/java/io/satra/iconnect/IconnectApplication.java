package io.satra.iconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(IconnectApplication.class, args);
	}

}
