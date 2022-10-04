package io.satra.iconnect.user_service.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import io.satra.iconnect.user_service.dto.QRCodeDTO;
import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.UpdatePasswordDTO;
import io.satra.iconnect.user_service.dto.UpdateProfileRequestDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.exception.MissingRefreshTokenException;
import io.satra.iconnect.user_service.exception.generic.BadRequestException;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import io.satra.iconnect.user_service.repository.UserRepository;
import io.satra.iconnect.user_service.utils.EncodingUtils;
import io.satra.iconnect.user_service.utils.JWTUtils;
import io.satra.iconnect.user_service.utils.TimeUtils;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWTUtils jwtUtils;

  @Override
  public Page<UserDTO> findAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(User::toDTO);
  }

  @Override
  public UserDTO getCurrentUser() throws EntityNotFoundException {
    String emailOrPhoneNumber = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    User currentUser = userRepository.findByEmailOrPhoneNumber(emailOrPhoneNumber, emailOrPhoneNumber)
        .orElseThrow(() -> new EntityNotFoundException("No user with email or phoneNumber %s found".formatted(emailOrPhoneNumber)));
    return currentUser.toDTO();
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
  public UserDTO findUserByEmailOrPhoneNumber(String email, String phoneNumber) throws EntityNotFoundException {
    User user = userRepository.findByEmailOrPhoneNumber(email, phoneNumber)
        .orElseThrow(() -> new EntityNotFoundException("No user with email %s or phoneNumber %s found!".formatted(email, phoneNumber)));
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
        .firstName(registerRequest.getFirstName())
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
  public UserDTO activateUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    updatedUser.setIsActive(Boolean.TRUE);

    updatedUser = userRepository.save(updatedUser);
    log.info("User with id {} activated!", id);

    return updatedUser.toDTO();
  }

  @Override
  public UserDTO deactivateUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    updatedUser.setIsActive(Boolean.FALSE);

    updatedUser = userRepository.save(updatedUser);
    log.info("User with id {} deactivated!", id);

    return updatedUser.toDTO();
  }

  @Override
  public UserDTO verifyMailOfUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    updatedUser.setIsEmailVerified(Boolean.TRUE);

    updatedUser = userRepository.save(updatedUser);
    log.info("Email of user with id {} verified!", id);

    return updatedUser.toDTO();
  }

  @Override
  public UserDTO refuteMailOfUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    updatedUser.setIsEmailVerified(Boolean.FALSE);

    updatedUser = userRepository.save(updatedUser);
    log.info("Email of user with id {} refuted!", id);

    return updatedUser.toDTO();
  }

  @Override
  public UserDTO verifyPhoneNumberOfUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    updatedUser.setIsPhoneVerified(Boolean.TRUE);

    updatedUser = userRepository.save(updatedUser);
    log.info("PhoneNumber of user with id {} verified!", id);

    return updatedUser.toDTO();
  }

  @Override
  public UserDTO refutePhoneNumberOfUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    updatedUser.setIsPhoneVerified(Boolean.FALSE);

    updatedUser = userRepository.save(updatedUser);
    log.info("PhoneNumber of user with id {} refuted!", id);

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
  public UserDTO deleteOTPFromUser(String id) throws EntityNotFoundException {
    User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with id %s found!".formatted(id)));
    updatedUser.setOtpCode(null);

    updatedUser = userRepository.save(updatedUser);
    log.info("Deleted OTP token from user with id {}", id);

    return updatedUser.toDTO();
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

  @Override
  public Map<String, String> refreshToken(HttpServletRequest request) throws MissingRefreshTokenException {
    String authorisationHeader = request.getHeader(AUTHORIZATION);

    if (authorisationHeader != null && authorisationHeader.startsWith("Bearer ")) {
      String refreshToken = authorisationHeader.substring("Bearer ".length());
      DecodedJWT decodedJWT = jwtUtils.decodeJWT(refreshToken);

      String phoneNumber = decodedJWT.getSubject();
      User user = userRepository.findByPhoneNumber(phoneNumber)
          .orElseThrow(() -> new EntityNotFoundException("No user with phoneNumber %s found!".formatted(phoneNumber)));

      String newAccessToken = jwtUtils.createAccessToken(request, user);

      return Map.of("accessToken", newAccessToken, "refreshToken", refreshToken);
    } else {
      throw new MissingRefreshTokenException();
    }
  }
}
