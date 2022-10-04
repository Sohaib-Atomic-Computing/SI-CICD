package io.satra.iconnect.user_service.service;


import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.UpdatePasswordDTO;
import io.satra.iconnect.user_service.dto.UpdateProfileRequestDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.exception.MissingRefreshTokenException;
import io.satra.iconnect.user_service.exception.generic.BadRequestException;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
   * This method is used to get the current authenticated user
   *
   * @return the current authenticated user
   * @throws EntityNotFoundException if no user is authenticated
   */
  UserDTO getCurrentUser() throws EntityNotFoundException;


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
   * Get a user by given email or phoneNumber
   *
   * @param email       the email of the user to be obtained
   * @param phoneNumber the phoneNumber of the user to be obtained
   * @return a {@link UserDTO}
   * @throws EntityNotFoundException if no user with given email or phoneNumber is found
   */
  UserDTO findUserByEmailOrPhoneNumber(String email, String phoneNumber) throws EntityNotFoundException;

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
   * Activate a user
   *
   * @param id the id of the user to be activated
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO activateUser(String id) throws EntityNotFoundException;

  /**
   * Deactivate a user
   *
   * @param id the id of the user to be deactivated
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO deactivateUser(String id) throws EntityNotFoundException;

  /**
   * Verify the mail of a user
   *
   * @param id the id of the user which email should be verified
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO verifyMailOfUser(String id) throws EntityNotFoundException;

  /**
   * Refute the mail of a user
   *
   * @param id the id of the user which email should be refuted
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO refuteMailOfUser(String id) throws EntityNotFoundException;

  /**
   * Verify phoneNumber of a user
   *
   * @param id the id of the user which phoneNumber should be verified
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO verifyPhoneNumberOfUser(String id) throws EntityNotFoundException;

  /**
   * Refute phoneNumber of a user
   *
   * @param id the id of the user which phoneNumber should be refuted
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO refutePhoneNumberOfUser(String id) throws EntityNotFoundException;

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
   * Delete OTP from given user
   *
   * @param id the id of the user the OTP should be deleted from
   * @return the updated {@link UserDTO}
   * @throws EntityNotFoundException if no user with given id is found
   */
  UserDTO deleteOTPFromUser(String id) throws EntityNotFoundException;

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

  /**
   * Refreshes the access token by utilizing the refresh token
   *
   * @param request the HTTP request including the authentication information
   * @return the newly created accessToken
   * @throws MissingRefreshTokenException if no refreshToken is present in the request
   * @throws EntityNotFoundException      if now user with phoneNumber from token is found
   */
  Map<String, String> refreshToken(HttpServletRequest request) throws MissingRefreshTokenException, EntityNotFoundException;
}
