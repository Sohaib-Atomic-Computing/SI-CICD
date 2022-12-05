package io.satra.iconnect.service.user;

import com.google.gson.Gson;
import io.satra.iconnect.dto.QRCodeDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.GenerateOTPDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.request.UpdateProfileRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.UserRepository;
import io.satra.iconnect.security.JWTUtils;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.utils.EncodingUtils;
import io.satra.iconnect.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    @Value("${iconnect.app.env}")
    private String env;

    /**
     * Login a user
     *
     * @param loginRequestDTO the user email & password
     * @return the logged-in user with the jwt token {@link JwtResponseDTO}
     * @throws BadRequestException if the user does not exist or the password is incorrect
     */
    @Override
    public JwtResponseDTO loginUser(LoginRequestDTO loginRequestDTO) throws BadRequestException {
       log.info("Logging in user with email: {}, and password: {}", loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());
         User user = userRepository.findByEmailOrMobile(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getEmailOrMobile())
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        // Generate JWT token
        String jwt = generateJWTToken(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());

        // check if the user has OTP
        if (user.getOtpCode() != null && !user.getOtpCode().isEmpty()) {
            log.info("User has OTP: {}", user.getOtpCode());

            // check if the otp is expired
            if (user.getOtpExpireAt().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("OTP is expired!");
            }

            // reset the OTP
            user.setOtpCode(null);
            userRepository.save(user);
        }

        return JwtResponseDTO.builder()
                .user(user.toDTO())
                .token(jwt)
                .type("Bearer")
                .build();
    }

    /**
     * Register a new user
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link UserDTO}
     * @throws BadRequestException if the user already exists
     */
    @Override
    public JwtResponseDTO register(RegisterRequestDTO registerRequestDTO) throws BadRequestException {
        log.info("Registering user with email: {} and mobile: {}", registerRequestDTO.getEmail(), registerRequestDTO.getMobile());
        // check if the user already exists
        if (userRepository.findByEmailOrMobile(registerRequestDTO.getEmail(), registerRequestDTO.getMobile()).isPresent()) {
            throw new BadRequestException("User with email %s or mobile %s already exists!".formatted(registerRequestDTO.getEmail(), registerRequestDTO.getMobile()));
        }
        User registeredUser = User.builder()
                .firstName(registerRequestDTO.getFirstName())
                .lastName(registerRequestDTO.getLastName())
                .email(registerRequestDTO.getEmail())
                .mobile(registerRequestDTO.getMobile())
                .role(UserRole.ROLE_USER)
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .build();

        // generate QR code
        try {
            registeredUser.setQrCode(generateQRCode(registeredUser));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // save the new user to the database
        registeredUser = userRepository.save(registeredUser);

        // Generate JWT token
        String jwt = generateJWTToken(registerRequestDTO.getEmail(), registerRequestDTO.getPassword());

        return JwtResponseDTO.builder()
                .user(registeredUser.toDTO())
                .token(jwt)
                .type("Bearer")
                .build();
    }

    /**
     * This method is used to get the current authenticated user
     *
     * @return the current authenticated user
     * @throws EntityNotFoundException if no user is authenticated
     */
    @Override
    public UserDTO getCurrentUser() throws EntityNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByEmailOrMobile(userPrincipal.getUsername(), userPrincipal.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        return currentUser.toDTO();
    }

    /**
     * This method is used to find a user by id
     *
     * @param id the id of the user to be obtained
     * @return the user with the given id {@link UserDTO}
     * @throws EntityNotFoundException if the user does not exist
     */
    @Override
    public UserDTO findUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user with id %s found".formatted(id)));
        return user.toDTO();
    }

    /**
     * This method is used to add a new admin user
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link UserDTO}
     * @throws BadRequestException if the user already exists
     */
    @Override
    public UserDTO addAdminUser(RegisterRequestDTO registerRequestDTO) throws BadRequestException {
        log.info("Registering admin with email: {} and mobile: {}", registerRequestDTO.getEmail(), registerRequestDTO.getMobile());
        // check if the user already exists
        if (userRepository.findByEmailOrMobile(registerRequestDTO.getEmail(), registerRequestDTO.getMobile()).isPresent()) {
            throw new BadRequestException("User with email %s or mobile %s already exists!".formatted(registerRequestDTO.getEmail(), registerRequestDTO.getMobile()));
        }
        User registeredUser = User.builder()
                .firstName(registerRequestDTO.getFirstName())
                .lastName(registerRequestDTO.getLastName())
                .email(registerRequestDTO.getEmail())
                .mobile(registerRequestDTO.getMobile())
                .role(UserRole.ROLE_ADMIN)
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .build();

        // save the new user to the database
        registeredUser = userRepository.save(registeredUser);

        return registeredUser.toDTO();
    }

    /**
     * This method is used to update the user information.
     * The user can only update his/her first name and last name.
     * The admin can update the first name, last name, email, mobile, isActive and role of the user.
     *
     * @param id                       the id of the user to be updated
     * @param updateProfileRequestDTO the user information to update
     * @return the updated user {@link UserDTO}
     * @throws EntityNotFoundException if the user does not exist
     */
    @Override
    public UserDTO updateUser(String id, UpdateProfileRequestDTO updateProfileRequestDTO) throws EntityNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));

        if (updateProfileRequestDTO.getFirstName() != null) {
            updatedUser.setFirstName(updateProfileRequestDTO.getFirstName());
        }
        if (updateProfileRequestDTO.getLastName() != null) {
            updatedUser.setLastName(updateProfileRequestDTO.getLastName());
        }

        // only admin can update the role, email, isActive and mobile
        if (userPrincipal.getUser().getRole() == UserRole.ROLE_ADMIN) {
            if (updateProfileRequestDTO.getRole() != null) {
                updatedUser.setRole(updateProfileRequestDTO.getRole());
            }

            if (updateProfileRequestDTO.getEmail() != null) {
                updatedUser.setEmail(updateProfileRequestDTO.getEmail());
            }

            if (updateProfileRequestDTO.getMobile() != null) {
                updatedUser.setMobile(updateProfileRequestDTO.getMobile());
            }

            if (updateProfileRequestDTO.getIsActive() != null) {
                updatedUser.setIsActive(updateProfileRequestDTO.getIsActive());
            }
        }

        // update the user
        updatedUser = userRepository.save(updatedUser);

        return updatedUser.toDTO();
    }

    /**
     * This method is used to delete a user
     *
     * @param id the id of the user to be deleted
     * @throws EntityNotFoundException if the user does not exist
     */
    @Override
    public void deleteUser(String id) throws EntityNotFoundException {
        try {
            userRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("No user with id %s found".formatted(id));
        }
    }

    /**
     * This method is used to find all users with pagination
     *
     * @param email the user email to be searched
     * @param mobile  the user mobile to be searched
     * @param firstName the user first name to be searched
     * @param lastName the user last name to be searched
     * @param pageable used for pagination
     * @return the list of users as a {@link Page} of {@link UserDTO}
     */
    @Override
    public Page<UserDTO> findAllUsers(String email, String mobile, String firstName, String lastName, Pageable pageable) {
        // prepare the filter specification
        Specification<User> specification = UserSpecifications.filterUsers(email, mobile, firstName, lastName);
        return userRepository.findAll(specification, pageable).map(User::toDTO);
    }

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
    @Override
    public void checkAndCreateAdminUser(String email, String password) throws Exception {
        if (userRepository.findByRole(UserRole.ROLE_ADMIN).isEmpty()) {
            log.info("No admin user found. Creating a new admin user with email: {} and password: {}", email, password);
            User adminUser = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email(email)
                    .mobile("0771234567")
                    .role(UserRole.ROLE_ADMIN)
                    .password(passwordEncoder.encode(password))
                    .build();

            userRepository.save(adminUser);
        }
    }

    /**
     * This method is used to get the users entities by given ids
     *
     * @param ids the ids of the users to be obtained
     * @return a {@link Set} of {@link User}
     */
    @Override
    public List<User> findUsersByIds(Set<String> ids) {
        return userRepository.findAllById(ids);
    }

    /**
     * This method is used to get the user entity by given id
     * @param id the id of the user to be obtained
     * @return a {@link User}
     * @throws EntityNotFoundException if no user with given id is found
     */
    @Override
    public User findUserEntityById(String id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user with given id %s found!".formatted(id)));
    }

    /**
     * This method is used to send OTP to the user
     *
     * @param generateOTPDTO the user mobile number to send the OTP
     * @return {@link ResponseDTO} with the status of the operation
     * @throws EntityNotFoundException if the user does not exist
     */
    @Override
    public ResponseDTO sendOTP(GenerateOTPDTO generateOTPDTO) throws EntityNotFoundException{
        // get the user by mobile number
        User user = userRepository.findByMobile(generateOTPDTO.getMobile())
                .orElseThrow(() -> new EntityNotFoundException("No user with given mobile number %s found!".formatted(generateOTPDTO.getMobile())));

        // generate a random 5 digit number
        Random rand = new Random();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 5);

        String otp = String.format("%05d", rand.nextInt(100000));

        if (env.equals("dev")) {
            user.setOtpCode(passwordEncoder.encode("00000"));
        } else {
            user.setOtpCode(passwordEncoder.encode(otp));
        }

        // TODO: send the OTP to the user

        // set the expiry time
        user.setOtpExpireAt(TimeUtils.convertDateToLocalDateTime(c.getTime()));
        // set the otp created time
        user.setOtpCreatedAt(LocalDateTime.now());

        // save the user
        userRepository.save(user);

        return ResponseDTO.builder()
                .message("Verification OTP has been sent to your mobile number.")
                .success(true)
                .build();
    }

    /**
     * Generate a JWT token for the user
     *
     * @param emailOrMobile the user email or mobile number
     * @param password the user password
     * @return the generated JWT token
     */
    private String generateJWTToken(String emailOrMobile, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrMobile, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    /**
     * Generate a QR code for the user
     *
     * @param user the user to generate the QR code for
     * @return the QR code {@link QRCodeDTO}
     * @throws NoSuchAlgorithmException if the algorithm is not found
     */
    private String generateQRCode(User user) throws NoSuchAlgorithmException {
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
