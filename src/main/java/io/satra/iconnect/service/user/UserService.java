package io.satra.iconnect.service.user;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.*;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * Logout a user
     *
     * @return true if the user is logged out successfully
     * @throws EntityNotFoundException if the user is not exists
     */
    boolean logout() throws EntityNotFoundException;

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
    Object getCurrentUser() throws EntityNotFoundException;

    /**
     * This method is used to update a user
     *
     * @param firstName         the user first name to update
     * @param lastName          the user last name to update
     * @param email             the user email to update
     * @param profilePicture    the user profile picture to update
     * @return the updated user {@link UserDTO}
     * @throws EntityNotFoundException if no user with given id is found
     */
    UserDTO updateMyProfile(String firstName, String lastName, String email, MultipartFile profilePicture) throws EntityNotFoundException, IOException;

    /**
     * This method is used to change the password of a user
     *
     * @param changePasswordDTO the user information to change the password
     * @@return the updated {@link UserDTO}
     * @throws EntityNotFoundException if no user is authenticated
     * @throws BadRequestException if the old password is incorrect
     * @throws BadRequestException if the new password is the same as the old password
     */
    UserDTO changePassword(ChangePasswordDTO changePasswordDTO) throws EntityNotFoundException, BadRequestException;

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
     * @param id                the id of the user to be updated
     * @param firstName         the user first name to update
     * @param lastName          the user last name to update
     * @param email             the user email to update
     * @param mobile            the user mobile to update
     * @param isActive          the user active status to update
     * @param role              the user roles to update
     * @param profilePicture    the user profile picture to update
     * @return the updated user {@link UserDTO}
     * @throws EntityNotFoundException if no user with given id is found
     */
    UserDTO updateUser(String id, String firstName, String lastName, String email, String mobile, Boolean isActive, UserRole role, MultipartFile profilePicture) throws EntityNotFoundException, IOException;

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
     * @throws BadRequestException if the email or mobile is not provided
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
     * This method is used to get the user active entity by given id
     * @param id the id of the user to be obtained
     * @return a {@link User}
     * @throws EntityNotFoundException if no user with given id is found or the user is not active
     */
    User findActiveUserEntityById(String id) throws EntityNotFoundException;


    /**
     * This method is used to send OTP to the user
     *
     * @param generateOTPDTO the user mobile number to send OTP
     * @return {@link ResponseDTO} with the status of the operation
     * @throws EntityNotFoundException if no user is authenticated
     */
    ResponseDTO sendOTP(GenerateOTPDTO generateOTPDTO) throws EntityNotFoundException;

    /**
     * This method is used to return the users vendors
     *
     * @return list of vendors that user has promotions for
     * @throws EntityNotFoundException if no user is authenticated
     */
    List<VendorDTO> getVendors() throws EntityNotFoundException;

    /**
     * This method is used to add user to the system
     *
     * @param addUserRequest the user information to be added to the system
     * @throws BadRequestException if there is a missing field
     */
    void addUser(AddUserRequest addUserRequest) throws BadRequestException;
}
