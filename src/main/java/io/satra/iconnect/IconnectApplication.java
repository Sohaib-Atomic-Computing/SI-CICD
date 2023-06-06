package io.satra.iconnect;

import io.satra.iconnect.service.user.UserService;
import io.satra.iconnect.utils.PropertyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IconnectApplication {
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(IconnectApplication.class, args);
	}

	@Bean
	public void init() {
		try {
			userService.checkAndCreateAdminUser(PropertyLoader.getDefaultAdminEmail(), PropertyLoader.getDefaultAdminPassword());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
