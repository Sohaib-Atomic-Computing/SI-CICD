package io.satra.iconnect.user_service.service;

import io.satra.iconnect.user_service.dto.DeviceTokenDTO;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import java.util.List;

public interface DeviceTokenService {

  /**
   * Get all existing device tokens
   *
   * @return a list of {@link DeviceTokenDTO}
   */
  List<DeviceTokenDTO> findAllDeviceTokens();

  /**
   * Get a device token with given id
   *
   * @param id the id of the device token to be obtained
   * @return the {@link DeviceTokenDTO} found
   * @throws EntityNotFoundException if no device token with given id is found
   */
  DeviceTokenDTO findDeviceTokenById(String id) throws EntityNotFoundException;

  /**
   * Add a new device token
   *
   * @param token the device token to be added
   * @return the newly added {@link DeviceTokenDTO}
   */
  DeviceTokenDTO addDeviceToken(String token);

  /**
   * Update an existing device token
   *
   * @param token the updated device token
   * @return the updated {@link DeviceTokenDTO}
   * @throws EntityNotFoundException if no device token with given id is found
   */
  DeviceTokenDTO updateDeviceToken(String token) throws EntityNotFoundException;
}
