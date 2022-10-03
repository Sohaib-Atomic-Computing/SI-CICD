package io.satra.iconnect.user_service.security;

import io.satra.iconnect.user_service.security.filter.CustomAuthenticationFilter;
import io.satra.iconnect.user_service.security.filter.CustomAuthorizationFilter;
import io.satra.iconnect.user_service.security.filter.JwtUnauthorizedExceptionHandler;
import io.satra.iconnect.user_service.service.UserDetailsServiceImpl;
import io.satra.iconnect.user_service.service.UserService;
import io.satra.iconnect.user_service.utils.JWTUtils;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsServiceImpl userDetailsService;
  private final PasswordEncoder passwordEncoder;

  private final UserService userService;
  private final JwtUnauthorizedExceptionHandler jwtUnauthorizedExceptionHandler;
  private final JWTUtils jwtUtils;

  private static final String[] AUTH_WHITELIST = {
      "/",
      "/api/v1/auth/login/**",
      "/api/v1/auth/register/**"
  };

  private static final String[] AUTH_AUTHENTICATED_LIST = {
      "/api/v1/auth/userinfo"
  };

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
    CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(userService, jwtUtils);
    customAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
    customAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");

    return customAuthenticationFilter;
  }

  @Bean
  public CustomAuthorizationFilter customAuthorizationFilter() throws Exception {
    return new CustomAuthorizationFilter(jwtUtils);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
    configuration.setAllowedHeaders(List.of("content-type", "authorization"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // disable CSRF protection
    http.csrf().disable();

    // enable CORS
    http.cors();

    // configure authentications
    http.authorizeRequests()
        .antMatchers(AUTH_WHITELIST).permitAll()
        .antMatchers(AUTH_AUTHENTICATED_LIST).authenticated()
        .anyRequest().denyAll();

    // configure exception handling
    http.exceptionHandling().authenticationEntryPoint(jwtUnauthorizedExceptionHandler);

    // add JWT authentication and authorization filter
    http.addFilter(customAuthenticationFilter());
    http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

    // configure session management
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
