package io.satra.iconnect.user_service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.satra.iconnect.user_service.dto.LoginRequestDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.entity.enums.ServiceType;
import io.satra.iconnect.user_service.service.UserService;
import io.satra.iconnect.user_service.utils.JWTUtils;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JWTUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final UserService userService;

  /**
   * This method parses the authentication HTTP request
   *
   * @param request the authentication HTTP request
   * @return parsed username and password
   * @throws AuthenticationServiceException if parsing goes wrong
   */
  private static LoginRequestDTO parseAuthenticationRequest(HttpServletRequest request) throws AuthenticationServiceException {
    try (BufferedReader reader = request.getReader()) {
      StringBuilder sb = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      String parsedReq = sb.toString();

      ObjectMapper mapper = new ObjectMapper();
      LoginRequestDTO loginRequestDTO = mapper.readValue(parsedReq, LoginRequestDTO.class);

      log.info("Parsed phoneNumber or email is: {}", loginRequestDTO.getPhoneNumberOrEmail());
      log.info("Parsed password or OTP is {}", loginRequestDTO.getPasswordOrCode());
      log.info("Parsed service type is {}", loginRequestDTO.getServiceType());

      return loginRequestDTO;
    } catch (Exception e) {
      throw new AuthenticationServiceException("Failed to parse authentication request body!");
    }
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      LoginRequestDTO loginRequestDTO = parseAuthenticationRequest(request);

      UserDTO userDTO = userService.findUserByEmailOrPhoneNumber(loginRequestDTO.getPhoneNumberOrEmail(), loginRequestDTO.getPhoneNumberOrEmail());

      Boolean isNormalLogin = loginRequestDTO.getServiceType() == ServiceType.LOGIN && userDTO.getIsActive() == Boolean.TRUE;
      Boolean isOTPLogin = loginRequestDTO.getServiceType() == ServiceType.OTP_VERIFY && userDTO.getIsActive() == Boolean.FALSE;

      if (isNormalLogin || isOTPLogin) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getPhoneNumberOrEmail(),
                loginRequestDTO.getPasswordOrCode())
        );

        log.info("User with id {} logged in successfully", userDTO.getId());

        return super.getAuthenticationManager().authenticate(authentication);
      }
    } catch (Exception e) {
      throw new InternalAuthenticationServiceException("Failed to authenticate user", e);
    }

    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
      throws IOException {
    LoginRequestDTO loginRequestDTO = parseAuthenticationRequest(request);
    UserDTO userDTO = userService.findUserByEmailOrPhoneNumber(loginRequestDTO.getPhoneNumberOrEmail(), loginRequestDTO.getPhoneNumberOrEmail());

    if (loginRequestDTO.getServiceType() == ServiceType.OTP_VERIFY) {
      userService.activateUser(userDTO.getId());
      userService.verifyPhoneNumberOfUser(userDTO.getId());
    }

    jwtUtils.addTokensToResponse(
        jwtUtils.createAccessToken(request, authentication),
        jwtUtils.createRefreshToken(request, authentication),
        response
    );
  }
}
