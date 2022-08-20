package com.iconnect.backend.controllers;

import com.iconnect.backend.dtos.TokensDTO;
import com.iconnect.backend.model.Tokens;
import com.iconnect.backend.services.TokenService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * @author Waqar 02/08/2020
 */
@CrossOrigin
@Api(value = "Authencation Back - End Services")
@RestController
@RequestMapping(value = "/api/v1/tokens", produces = "application/json")
public class TokensController {

  @Autowired
  private TokenService tokenService;

  public TokensController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @PostMapping
  public ResponseEntity<Tokens> addDeviceToken(@RequestBody(required = true) TokensDTO tokensDTO) {
    Tokens deviceToken = tokenService.add(tokensDTO.getDeviceToken());
    URI uri = URI.create("/tokens/" + deviceToken.getId());
    return ResponseEntity.created(uri).body(deviceToken);
  }

  @PutMapping
  public ResponseEntity<Tokens> updateDeviceToken(@RequestBody(required = true) TokensDTO tokensDTO) {
    Tokens deviceToken = tokenService.update(tokensDTO.getDeviceToken());
    return ResponseEntity.accepted().body(deviceToken);
  }

}
