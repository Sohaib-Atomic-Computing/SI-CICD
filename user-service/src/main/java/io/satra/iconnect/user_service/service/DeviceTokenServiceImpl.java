package io.satra.iconnect.user_service.service;

import io.satra.iconnect.user_service.dto.DeviceTokenDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.entity.DeviceToken;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import io.satra.iconnect.user_service.repository.DeviceTokenRepository;
import io.satra.iconnect.user_service.utils.AuthenticationUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenServiceImpl implements DeviceTokenService {

  private final DeviceTokenRepository deviceTokenRepository;

  private final UserService userService;

  @Override
  public List<DeviceTokenDTO> findAllDeviceTokens() {
    return deviceTokenRepository.findAll().stream().map(DeviceToken::toDTO).toList();
  }

  @Override
  public DeviceTokenDTO findDeviceTokenById(String id) throws EntityNotFoundException {
    DeviceToken deviceToken = deviceTokenRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Token with id %s not found".formatted(id)));
    return deviceToken.toDTO();
  }

  @Override
  public DeviceTokenDTO addDeviceToken(String token) {
    UserDTO userDTO = userService.findUserById(AuthenticationUtils.getAuthenticatedUserId());

    DeviceToken deviceToken = DeviceToken.builder()
        .user(userDTO.toEntity())
        .token(token)
        .build();

    deviceToken = deviceTokenRepository.save(deviceToken);
    log.info("New Device Token added: {}", deviceToken);

    return deviceToken.toDTO();
  }

  @Override
  public DeviceTokenDTO updateDeviceToken(String token) {
    UserDTO userDTO = userService.findUserById(AuthenticationUtils.getAuthenticatedUserId());

    DeviceToken deviceToken = deviceTokenRepository
        .findByUser(userDTO.toEntity())
        .orElseThrow(() -> new EntityNotFoundException("No token for user with id %s found!".formatted(userDTO.getId())));
    deviceToken.setToken(token);

    deviceToken = deviceTokenRepository.save(deviceToken);
    log.info("Device token updated: {}", deviceToken);

    return deviceToken.toDTO();
  }
}
