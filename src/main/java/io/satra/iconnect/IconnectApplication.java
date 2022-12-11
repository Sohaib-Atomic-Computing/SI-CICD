package io.satra.iconnect;

import io.satra.iconnect.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IconnectApplication {
	@Value("${iconnect.app.default.admin.email}")
	private String adminEmail;
	@Value("${iconnect.app.default.admin.password}")
	private String adminPassword;
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(IconnectApplication.class, args);
	}

	@Bean
	public void init() {
		try {
			userService.checkAndCreateAdminUser(adminEmail, adminPassword);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
