package io.satra.iconnect.user_service.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.satra.iconnect.user_service.dto.JwtResponseDTO;
import io.satra.iconnect.user_service.security.UserPrincipal;
import io.satra.iconnect.user_service.utils.TimeUtils;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProvider {

  @Value("${iconnect.app.jwtSecret}")
  private String jwtSecret;

  @Value("${iconnect.app.jwtExpiration}")
  private int jwtExpiration;

  public JwtResponseDTO generateJwtToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    return doGenerateTokenResponse(userPrincipal.getUsername(), null);
  }


  public JwtResponseDTO generateJwtToken(String username, String refreshToken) {
    return doGenerateTokenResponse(username, refreshToken);
  }

  public boolean validateJwtToken(String token) {
    try {
      getJwtParser().parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token", e);
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token", e);
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token", e);
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty", e);
    }

    return false;
  }

  public String getUserNameFromJwtToken(String token) {
    return getJwtParser()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  private Key getSecretKey() {
    return new SecretKeySpec(
        Base64.getDecoder().decode(jwtSecret),
        SignatureAlgorithm.HS512.getJcaName()
    );
  }

  private JwtParser getJwtParser() {
    return Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build();
  }

  private JwtResponseDTO doGenerateTokenResponse(String subject, String refreshToken) {
    LocalDateTime issueTime = LocalDateTime.now();
    LocalDateTime expiryTime = issueTime.plusSeconds(jwtExpiration);

    String token = Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(TimeUtils.convertLocalDateTimeToDate(issueTime))
        .setExpiration(TimeUtils.convertLocalDateTimeToDate(expiryTime))
        .signWith(getSecretKey())
        .compact();

    return JwtResponseDTO.builder()
        .issueTime(issueTime)
        .expiryTime(expiryTime)
        .accessToken(token)
        .refreshToken(refreshToken)
        .build();
  }
}
