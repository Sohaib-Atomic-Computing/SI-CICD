package io.satra.iconnect.user_service.security.jwt;

import io.jsonwebtoken.Jwts;
import io.satra.iconnect.user_service.dto.JwtResponseDTO;
import io.satra.iconnect.user_service.security.UserPrincipal;
import java.time.LocalDateTime;
import java.util.Date;
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

    LocalDateTime issueTime = LocalDateTime.now();
    LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(jwtExpiration);

    //TODO need to verify Expiration
    String token = Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(issueTime)
        .setExpiration(expiryTime)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();

    JwtResponseDTO jwt = new JwtResponseDTO();
    jwt.setIssueTime(issueTime);
    jwt.setExpiryTime(expiryTime);
    jwt.setAccessToken(token);
    jwt.setIssueTime(new Date());

    return jwt;
  }


  public JwtResponseDTO generateJwtToken(String username, String refreshtoken) {

    Date issuetime = new Date();
    Date expirytime = new Date((new Date()).getTime() + jwtExpiration * 1000);

    //TODO need to verify Expiration
    String token = Jwts.builder()
        .setSubject((username))
        .setIssuedAt(issuetime)
        .setExpiration(expirytime)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();

    JwtResponseDTO jwt = new JwtResponseDTO();
    jwt.setIssueTime(issuetime);
    jwt.setExpiryTime(expirytime);
    jwt.setRefreshToken(refreshtoken);
    jwt.setAccessToken(token);
    jwt.setIssueTime(new Date());

    return jwt;
  }


  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature -> Message: {} ", e);
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token -> Message: {}", e);
    } catch (ExpiredJwtException e) {
      logger.error("Expired JWT token -> Message: {}", e);
    } catch (UnsupportedJwtException e) {
      logger.error("Unsupported JWT token -> Message: {}", e);
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty -> Message: {}", e);
    }

    return false;
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody().getSubject();
  }

}
