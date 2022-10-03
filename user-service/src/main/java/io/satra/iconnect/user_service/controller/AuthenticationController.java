package io.satra.iconnect.user_service.controller;

import io.satra.iconnect.user_service.dto.GenerateOTPDTO;
import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.ResponseDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.exception.MissingRefreshTokenException;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import io.satra.iconnect.user_service.service.UserService;
import java.net.URI;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  private final UserService userService;

  /**
   * This endpoint responds with the current authenticated user
   *
   * @return current authenticated {@link UserDTO}
   * @throws EntityNotFoundException if no user is authenticated
   */
  @GetMapping("/userinfo")
  public ResponseEntity<UserDTO> getUserInfo() throws EntityNotFoundException {
    return ResponseEntity.ok(userService.getCurrentUser());
  }

  /**
   * This endpoint registers a new user to the application.
   *
   * @param registerRequestDTO the user information used for registration
   * @return newly registered {@link UserDTO}
   */
  @PostMapping("/register")
  public ResponseEntity<UserDTO> saveUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
    UserDTO registeredUser = userService.registerNewUser(registerRequestDTO);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users" + registeredUser.getId()).toUriString());
    return ResponseEntity.created(uri).body(registeredUser);
  }

  /**
   * This endpoint responds with a newly created access token for the given refresh token
   *
   * @param request the http request
   * @return newly created access token and refresh token
   * @throws MissingRefreshTokenException if no valid refresh token is given
   */
  @GetMapping("/token/refresh")
  public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) throws MissingRefreshTokenException {
    Map<String, String> newAccessTokenResponse = userService.refreshToken(request);

    return ResponseEntity.ok(newAccessTokenResponse);
  }

  /**
   * Generate a one-time-password (OTP) for a user
   *
   * @param generateOTPDTO contains the phoneNumber of the user in order to generate a unique OTP
   * @return a {@link ResponseDTO} with the generated OTP
   */
  @PostMapping(value = "/otp/generate")
  public ResponseEntity<ResponseDTO> generateOTP(@RequestBody GenerateOTPDTO generateOTPDTO) {
    Boolean isOTPGenerated = userService.generateOTPByPhoneNumber(generateOTPDTO.getPhoneNumber());
    ResponseDTO responseDTO = ResponseDTO.builder()
        .message("OTP Generated")
        .success(Boolean.TRUE)
        .data(isOTPGenerated)
        .build();

    return ResponseEntity.ok(responseDTO);
  }
}
