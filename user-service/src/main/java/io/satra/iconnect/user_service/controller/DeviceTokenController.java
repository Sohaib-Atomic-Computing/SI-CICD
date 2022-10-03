package io.satra.iconnect.user_service.controller;

import io.satra.iconnect.user_service.dto.DeviceTokenDTO;
import io.satra.iconnect.user_service.service.DeviceTokenService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {

  private final DeviceTokenService deviceTokenService;

  /**
   * This endpoint obtains a device token with given id
   *
   * @param id the id of the device token to be obtained
   * @return the {@link DeviceTokenDTO}
   */
  @GetMapping("/{id}")
  public ResponseEntity<DeviceTokenDTO> getDeviceTokenById(@PathVariable String id) {
    return ResponseEntity.ok(deviceTokenService.findDeviceTokenById(id));
  }

  /**
   * This endpoint add a new device token to the application.
   *
   * @param deviceTokenDTO the device token information used for creation
   * @return newly added {@link DeviceTokenDTO}
   */
  @PostMapping
  public ResponseEntity<DeviceTokenDTO> addDeviceToken(@RequestBody DeviceTokenDTO deviceTokenDTO) {
    DeviceTokenDTO addedDeviceToken = deviceTokenService.addDeviceToken(deviceTokenDTO.getToken());
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/device-tokens/" + addedDeviceToken.getId()).toUriString());

    return ResponseEntity.created(uri).body(addedDeviceToken);
  }

  /**
   * This endpoint updates an existing device token.
   *
   * @param deviceTokenDTO the device token information used for update
   * @return the updated {@link DeviceTokenDTO}
   */
  @PutMapping
  public ResponseEntity<DeviceTokenDTO> updateDeviceToken(@RequestBody DeviceTokenDTO deviceTokenDTO) {
    DeviceTokenDTO updatedDeviceToken = deviceTokenService.updateDeviceToken(deviceTokenDTO.getToken());
    return ResponseEntity.ok(updatedDeviceToken);
  }
}
