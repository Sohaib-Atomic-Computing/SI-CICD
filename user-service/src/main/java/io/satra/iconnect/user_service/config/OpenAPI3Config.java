package io.satra.iconnect.user_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User Service API",
        version = "1.0.0",
        contact = @Contact(
            name = "Satra", email = "info@satra.io", url = "https://www.satra.io"
        ),
        license = @License(
            name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        termsOfService = "https://www.satra.io/privacy-policy",
        description = "User Service API to register new users, delete existing users, obtain user data and update user data"
    ),
    servers = @Server(
        url = "http://localhost:8080/users/",
        description = "Development"
    )
)
public class OpenAPI3Config {

  @Bean
  public OpenAPI customizeOpenAPI() {
    final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement()
            .addList(securitySchemeName))
        .components(new Components()
            .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
  }
}
