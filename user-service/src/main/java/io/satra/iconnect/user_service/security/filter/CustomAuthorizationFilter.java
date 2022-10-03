package io.satra.iconnect.user_service.security.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.satra.iconnect.user_service.utils.JWTUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

  private final JWTUtils jwtUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (!request.getServletPath().equals("/api/v1/auth/login") && !request.getServletPath().equals("/api/v1/user/token/refresh") && !request.getServletPath()
        .equals("/api/v1/register")) {
      String authorisationHeader = request.getHeader(AUTHORIZATION);
      if (authorisationHeader != null && authorisationHeader.startsWith("Bearer ")) {
        String token = authorisationHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = jwtUtils.decodeJWT(token);

        String username = decodedJWT.getSubject();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
