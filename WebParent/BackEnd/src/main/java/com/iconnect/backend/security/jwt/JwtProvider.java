package com.iconnect.backend.security.jwt;


import com.iconnect.backend.dtos.JwtResponse;
import com.iconnect.backend.security.services.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

  @Value("${fuller.app.jwtSecret}")
  private String jwtSecret;

  @Value("${fuller.app.jwtExpiration}")
  private int jwtExpiration;

  public JwtResponse generateJwtToken(Authentication authentication) {

    UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();


    Date issuetime = new Date();
    Date expirytime = new Date((new Date()).getTime() + jwtExpiration * 1000);

    //TODO need to verify Expiration
    String token = Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(issuetime)
            .setExpiration(expirytime)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();

    JwtResponse jwt = new JwtResponse();
    jwt.setIssuetime(issuetime);
    jwt.setExpirytime(expirytime);
    jwt.setToken(token);
    jwt.setIssuetime(new Date());

    return jwt;
  }


  public JwtResponse generateJwtToken(String username, String refreshtoken) {


    Date issuetime = new Date();
    Date expirytime = new Date((new Date()).getTime() + jwtExpiration * 1000);

    //TODO need to verify Expiration
    String token = Jwts.builder()
            .setSubject((username))
            .setIssuedAt(issuetime)
            .setExpiration(expirytime)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();

    JwtResponse jwt = new JwtResponse();
    jwt.setIssuetime(issuetime);
    jwt.setExpirytime(expirytime);
    jwt.setRefreshtoken(refreshtoken);
    jwt.setToken(token);
    jwt.setIssuetime(new Date());

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