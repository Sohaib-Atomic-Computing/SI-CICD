package io.satra.iconnect.user_service.controller;

import io.satra.iconnect.user_service.dto.DeviceTokenDTO;
import io.satra.iconnect.user_service.entity.DeviceToken;
import io.satra.iconnect.user_service.service.DeviceTokenService;
import io.swagger.annotations.Api;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Waqar 02/08/2020
 */
@CrossOrigin
@Api(value = "Authencation Back - End Services")
@RestController
@RequestMapping(value = "/api/v1/tokens", produces = "application/json")
public class TokensController {

  @Autowired
  private DeviceTokenService deviceTokenService;

  public TokensController(DeviceTokenService deviceTokenService) {
    this.deviceTokenService = deviceTokenService;
  }

  @PostMapping
  public ResponseEntity<DeviceToken> addDeviceToken(@RequestBody(required = true) DeviceTokenDTO tokensDTO) {
    DeviceToken deviceToken = deviceTokenService.addDeviceToken(tokensDTO.getToken());
    URI uri = URI.create("/tokens/" + deviceToken.getId());
    return ResponseEntity.created(uri).body(deviceToken);
  }

  @PutMapping
  public ResponseEntity<DeviceToken> updateDeviceToken(@RequestBody(required = true) DeviceTokenDTO tokensDTO) {
    DeviceToken deviceToken = deviceTokenService.updateDeviceToken(tokensDTO.getToken());
    return ResponseEntity.accepted().body(deviceToken);
  }

}
