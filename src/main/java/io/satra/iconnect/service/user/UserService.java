package io.satra.iconnect.service.user;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.GenerateOTPDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.request.UpdateProfileRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserService {

    /**
     * Login a user
     *
     * @param loginRequestDTO the user email & password
     * @return the logged-in user with the jwt token {@link JwtResponseDTO}
     * @throws BadRequestException if the user does not exist or the password is incorrect
     */
    JwtResponseDTO loginUser(LoginRequestDTO loginRequestDTO) throws BadRequestException;

    /**
     * Register a new user
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link JwtResponseDTO}
     * @throws BadRequestException if the user already exists
     */
    JwtResponseDTO register(RegisterRequestDTO registerRequestDTO) throws BadRequestException;

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
     * This method is used to add a new admin user
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link UserDTO}
     * @throws BadRequestException
     */
    UserDTO addAdminUser(RegisterRequestDTO registerRequestDTO) throws BadRequestException;

    /**
     * This method is used to update a user
     *
     * @param id                       the id of the user to be updated
     * @param updateProfileRequestDTO the user information to update
     * @return the updated user {@link UserDTO}
     * @throws EntityNotFoundException if no user with given id is found
     */
    UserDTO updateUser(String id, UpdateProfileRequestDTO updateProfileRequestDTO) throws EntityNotFoundException;

    /**
     * Delete an user
     *
     * @param id the id of the user to be deleted
     * @throws EntityNotFoundException if no user with given id is found
     */
    void deleteUser(String id) throws EntityNotFoundException;

    /**
     * This method is used to get all users
     *
     * @param email the user email to be searched
     * @param mobile  the user mobile to be searched
     * @param firstName the user first name to be searched
     * @param lastName the user last name to be searched
     * @param pageable used for pagination
     * @return a {@link Page} of {@link UserDTO}
     */
    Page<UserDTO> findAllUsers(String email, String mobile, String firstName, String lastName, Pageable pageable);

    /**
     * This method is used check if the user exists by given email or mobile
     *
     * @param email the user email to be searched
     * @param mobile the user mobile to be searched
     * @return true if the user exists, false otherwise
     * @throws EntityNotFoundException
     */
    Boolean userExists(String email, String mobile) throws EntityNotFoundException;

    /**
     * This method checks if the main user admin is exists or not.
     * If not, it creates a new admin user with the given email and password
     *
     * @param email the email of the admin user
     *              if null, the default email will be used
     * @param password the password of the admin user
     *                 if null, the default password will be used
     * @throws Exception if the admin user is not created
     */
    void checkAndCreateAdminUser(String email, String password) throws Exception;

    /**
     * This method is used to get the users entities by given ids
     *
     * @param ids the ids of the users to be obtained
     * @return a {@link Set} of {@link User}
     */
    List<User> findUsersByIds(Set<String> ids);

    /**
     * This method is used to get the user entity by given id
     * @param id the id of the user to be obtained
     * @return a {@link User}
     * @throws EntityNotFoundException if no user with given id is found
     */
    User findUserEntityById(String id) throws EntityNotFoundException;

    /**
     * This method is used to send OTP to the user
     *
     * @param generateOTPDTO the user mobile number to send OTP
     * @return {@link ResponseDTO} with the status of the operation
     * @throws EntityNotFoundException if no user is authenticated
     */
    ResponseDTO sendOTP(GenerateOTPDTO generateOTPDTO) throws EntityNotFoundException;

}
