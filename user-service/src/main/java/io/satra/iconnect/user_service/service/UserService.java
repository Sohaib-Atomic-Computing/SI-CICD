package io.satra.iconnect.user_service.service;


import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.UpdatePasswordDTO;
import io.satra.iconnect.user_service.dto.UpdateProfileRequestDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.exception.generic.BadRequestException;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  /**
   * Get all users with pagination
   *
   * @param pageable used for pagination
   * @return a {@link Page} of {@link UserDTO}
   */
  Page<UserDTO> findAllUsers(Pageable pageable);

  /**
   * Get a user by given id
   *
   * @param id the id of the user to be obtained
   * @return a {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO findUserById(String id);

  /**
   * Get a user by given email
   *
   * @param email the email of the user to be obtained
   * @return a {@link UserDTO}
   * @throws EntityNotFoundException if no user with given email is found
   */
  UserDTO findUserByEmail(String email) throws EntityNotFoundException;

  /**
   * Get a user by given phoneNumber
   *
   * @param phoneNumber the phoneNumber of the user to be obtained
   * @return a {@link UserDTO}
   * @throws EntityNotFoundException if no user with given phoneNumber is found
   */
  UserDTO findUserByPhoneNumber(String phoneNumber) throws EntityNotFoundException;

  /**
   * Register a new user
   *
   * @param registerRequestDTO the information for the user to be registered
   * @return the newly registered {@link UserDTO}
   * @throws BadRequestException if user with same email or phoneNumber already exists
   */
  UserDTO registerNewUser(RegisterRequestDTO registerRequestDTO) throws BadRequestException;

  /**
   * Update an existing user
   *
   * @param id                   the id of the user to be updated
   * @param updateProfileRequest the updated user information
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with the given id is found
   */
  UserDTO updateUser(String id, UpdateProfileRequestDTO updateProfileRequest) throws EntityNotFoundException;

  /**
   * Delete an user
   *
   * @param id the id of the user to be deleted
   * @throws EntityNotFoundException if no user with given id is found
   */
  void deleteUser(String id) throws EntityNotFoundException;

  /**
   * Search for users by given phoneNumber
   *
   * @param phoneNumber the phoneNumber to be searched for
   * @param pageable    used for pagination
   * @return @return a {@link Page} of {@link UserDTO}
   */
  Page<UserDTO> searchUsers(String phoneNumber, Pageable pageable);

  /**
   * Generate an one-time-password (OTP)
   *
   * @param phoneNumber the phoneNumber of the user used to create the OTP
   * @return true if OTP was generated successfully, false otherwise
   * @throws EntityNotFoundException if no user with given phoneNumber is found
   */
  Boolean generateOTPByPhoneNumber(String phoneNumber) throws EntityNotFoundException;

  /**
   * Updates the password of a user
   *
   * @param id                    the id of the user to be updated
   * @param updatePasswordRequest the updated password request containing the old and new password for validation purposes
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   * @throws BadRequestException     if current password does not match the users current password
   */
  UserDTO updatePassword(String id, UpdatePasswordDTO updatePasswordRequest) throws EntityNotFoundException, BadRequestException;
}
