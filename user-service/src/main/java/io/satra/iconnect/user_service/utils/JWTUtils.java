package io.satra.iconnect.user_service.utils;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.satra.iconnect.user_service.security.UserPrincipal;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JWTUtils {

  @Value("${iconnect.app.jwtSecret}")
  private String secret;

  @Value("${iconnect.app.jwtAccessTokenExpiration}")
  private Long accessTokenExpiration;

  @Value("${iconnect.app.jwtSecret}")
  private Long refreshTokenExpiration;

  /**
   * This method adds the access token and the refresh token to the HTTP response body
   *
   * @param accessToken  the access token to be added
   * @param refreshToken the refresh token to be added
   * @param response     the HTTP response
   * @throws IOException if tokens cannot be added to HTTP response body
   */
  public void addTokensToResponse(String accessToken, String refreshToken, HttpServletResponse response) throws IOException {
    Map<String, String> tokens = new HashMap<>();
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);

    response.setContentType(APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
  }

  /**
   * This method creates an access token for a given authentication
   *
   * @param request        the HTTP authentication request
   * @param authentication the authentication
   * @return the access token
   */
  public String createAccessToken(HttpServletRequest request, Authentication authentication) {
    UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
    Algorithm algorithm = Algorithm.HMAC256(secret);

    return createToken(request, user, new Date(System.currentTimeMillis() + accessTokenExpiration))
        .sign(algorithm);
  }

  /**
   * This method creates an access token for a given user
   *
   * @param request the HTTP authentication request
   * @param user    the user
   * @return the access token
   */
  public String createAccessToken(HttpServletRequest request, io.satra.iconnect.user_service.entity.User user) {
    Algorithm algorithm = Algorithm.HMAC256(secret);

    return createToken(request, user, new Date(System.currentTimeMillis() + accessTokenExpiration))
        .sign(algorithm);
  }

  /**
   * This method creates a refresh token for a given authentication
   *
   * @param request        the HTTP authentication request
   * @param authentication the authentication
   * @return the refresh token
   */
  public String createRefreshToken(HttpServletRequest request, Authentication authentication) {
    UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
    Algorithm algorithm = Algorithm.HMAC256(secret);

    return createToken(request, user, new Date(System.currentTimeMillis() + refreshTokenExpiration)).sign(algorithm);
  }

  private JWTCreator.Builder createToken(HttpServletRequest request, UserPrincipal userPrincipal, Date expirationDate) {
    return JWT.create()
        .withSubject(userPrincipal.getUser().getPhoneNumber())
        .withExpiresAt(expirationDate)
        .withIssuer(request.getRequestURL().toString());
  }

  private JWTCreator.Builder createToken(
      HttpServletRequest request, io.satra.iconnect.user_service.entity.User user, Date expirationDate) {
    return JWT.create()
        .withSubject(user.getPhoneNumber())
        .withExpiresAt(expirationDate)
        .withIssuer(request.getRequestURL().toString());
  }

  /**
   * This method decodes a JSON Web Token
   *
   * @param token to token to be decoded
   * @return the decoded token
   */
  public DecodedJWT decodeJWT(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secret);
    JWTVerifier verifier = JWT.require(algorithm).build();

    return verifier.verify(token);
  }
}
