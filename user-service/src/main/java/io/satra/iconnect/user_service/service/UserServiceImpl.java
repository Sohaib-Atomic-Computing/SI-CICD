package io.satra.iconnect.user_service.service;

import com.google.gson.Gson;
import io.satra.iconnect.user_service.Utils.EncodingUtils;
import io.satra.iconnect.user_service.Utils.TimeUtils;
import io.satra.iconnect.user_service.dto.QRCodeDTO;
import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.UpdatePasswordDTO;
import io.satra.iconnect.user_service.dto.UpdateProfileRequestDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.exception.generic.BadRequestException;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import io.satra.iconnect.user_service.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Page<UserDTO> findAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(User::toDTO);
  }

  @Override
  public UserDTO findUserById(String id) throws EntityNotFoundException {
    User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with id %s found".formatted(id)));
    return user.toDTO();
  }

  @Override
  public UserDTO findUserByEmail(String email) throws EntityNotFoundException {
    User user = userRepository.findByEmailAndIsActive(email, Boolean.TRUE)
        .orElseThrow(() -> new EntityNotFoundException("No user with email %s found!".formatted(email)));
    return user.toDTO();
  }

  @Override
  public UserDTO findUserByPhoneNumber(String phoneNumber) throws EntityNotFoundException {
    User user = userRepository.findByPhoneNumberAndIsActive(phoneNumber, Boolean.TRUE)
        .orElseThrow(() -> new EntityNotFoundException("No user with phoneNumber %s found!".formatted(phoneNumber)));
    return user.toDTO();
  }

  @Override
  public UserDTO registerNewUser(RegisterRequestDTO registerRequest) throws BadRequestException {
    // check if user with same email already exists
    Optional<User> userEmailOptional = userRepository.findByEmail(registerRequest.getEmail().toLowerCase());
    userEmailOptional.ifPresent(
        user -> {
          throw new BadRequestException("User with email %s already exists!".formatted(registerRequest.getEmail()));
        }
    );

    // check if user with same phoneNumber already exists
    Optional<User> userPhoneNumberOptional = userRepository.findByPhoneNumber(registerRequest.getPhoneNumber().toLowerCase());
    userPhoneNumberOptional.ifPresent(
        user -> {
          throw new BadRequestException("Phone Number Already Exist");
        }
    );

    // create new user entity with given user information
    User registeredUser = User.builder()
        .email(registerRequest.getEmail())
        .phoneNumber(registerRequest.getPhoneNumber())
        .genderType(registerRequest.getGenderType())
        .firstName(registerRequest.getFristName())
        .lastName(registerRequest.getLastName())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .dpUrl(registerRequest.getDpUrl())
        .resetToken(UUID.randomUUID().toString())
        .otpCode(passwordEncoder.encode("00000"))
        .build();

    // create QR code from user information
    registeredUser.setQrCode(createQRCode(registeredUser));

    // save new user to database
    registeredUser = userRepository.save(registeredUser);
    log.info("New User registered: {}", registeredUser);

    return registeredUser.toDTO();
  }

  @Override
  public UserDTO updateUser(String id, UpdateProfileRequestDTO updateProfileRequest) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));

    if (updateProfileRequest.getGenderType() != null) {
      updatedUser.setGenderType(updateProfileRequest.getGenderType());
    }
    if (updateProfileRequest.getFristName() != null) {
      updatedUser.setFirstName(updateProfileRequest.getFristName());
    }
    if (updateProfileRequest.getLastName() != null) {
      updatedUser.setLastName(updateProfileRequest.getLastName());
    }
    if (updateProfileRequest.getDpUrl() != null) {
      updatedUser.setDpUrl(updateProfileRequest.getDpUrl());
    }

    updatedUser = userRepository.save(updatedUser);
    log.info("Updated user: {}", updatedUser);

    return updatedUser.toDTO();
  }

  @Override
  public void deleteUser(String id) throws EntityNotFoundException {
    try {
      userRepository.deleteById(id);
    } catch (IllegalArgumentException e) {
      throw new EntityNotFoundException("No user with id %s found".formatted(id));
    }
  }

  @Override
  public Page<UserDTO> searchUsers(String phoneNumber, Pageable pageable) {
    return userRepository.findByPhoneNumberIgnoreCaseContains(phoneNumber, pageable).map(User::toDTO);
  }

  @Override
  public Boolean generateOTPByPhoneNumber(String phoneNumber) {
    User user = userRepository.findByPhoneNumber(phoneNumber.toLowerCase())
        .orElseThrow(() -> new EntityNotFoundException("No user with phoneNumber %s found".formatted(phoneNumber)));
    user.setOtpCode(passwordEncoder.encode("00000"));

    user = userRepository.save(user);
    log.info("Generated OTP for user with id {}", user.getId());

    return true;
  }

  @Override
  public UserDTO updatePassword(String id, UpdatePasswordDTO updatePasswordRequest) throws EntityNotFoundException, BadRequestException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with id %s found!".formatted(id)));

    if (updatePasswordRequest.getCurrentPassword() != null) {
      if (passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), updatedUser.getPassword())) {
        if (updatePasswordRequest.getNewPassword() != null) {
          updatedUser.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        }
      } else {
        throw new BadRequestException("Current password does not match!");
      }
    }

    updatedUser = userRepository.save(updatedUser);
    log.info("Updated password of user with id {}", updatedUser.getId());

    return updatedUser.toDTO();
  }

  private String createQRCode(User user) {
    Gson gson = new Gson();

    QRCodeDTO qrCodeDTO = QRCodeDTO.builder()
        .uniqueID(user.getId())
        .timestamp(TimeUtils.getCurrentDateFormatted())
        .randomID(UUID.randomUUID().toString().replace("-", "").substring(0, 8))
        .build();

    String output = gson.toJson(qrCodeDTO);

    try {
      return EncodingUtils.encodeBase64(output);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
