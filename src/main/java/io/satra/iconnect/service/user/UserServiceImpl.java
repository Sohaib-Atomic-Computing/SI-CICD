package io.satra.iconnect.service.user;

import com.google.gson.Gson;
import io.satra.iconnect.dto.QRCodeDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.GenerateOTPDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.Promotion;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.UserRepository;
import io.satra.iconnect.security.JWTUtils;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.service.validator.ValidatorService;
import io.satra.iconnect.utils.EncodingUtils;
import io.satra.iconnect.utils.FileUtils;
import io.satra.iconnect.utils.PropertyLoader;
import io.satra.iconnect.utils.TimeUtils;
import io.satra.iconnect.utils.sms.SMSSender;
import io.satra.iconnect.utils.sms.SMSSenderCequens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidatorService validatorService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Login a user
     *
     * @param loginRequestDTO the user email & password
     * @return the logged-in user with the jwt token {@link JwtResponseDTO}
     * @throws BadRequestException if the user does not exist or the password is incorrect
     */
    @Override
    public JwtResponseDTO loginUser(LoginRequestDTO loginRequestDTO) throws BadRequestException {
        User user = userRepository.findFirstByEmailOrMobile(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getEmailOrMobile())
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        log.debug("User Mobile: {}", user.getMobile());

        // Generate JWT token
        String jwt = generateJWTToken(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());
        log.debug("User JWT: {}", jwt);

        // check if the user has OTP
        if (user.getOtpCode() != null && !user.getOtpCode().isEmpty()) {
            log.debug("User has OTP: {}", user.getOtpCode());

            // check if the otp is expired
            if (user.getOtpExpireAt().isBefore(LocalDateTime.now())) {
                log.error("OTP is expired!");
                throw new BadRequestException("OTP is expired!");
            }

            // reset the OTP
            user.setOtpCode(null);
        }

        user.setToken(jwt);
        userRepository.save(user);

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
        if (userRepository.findFirstByEmailOrMobile(registerRequestDTO.getEmail(), registerRequestDTO.getMobile()).isPresent()) {
            throw new BadRequestException(String.format("User with email: %s or mobile: %s already exists", registerRequestDTO.getEmail(), registerRequestDTO.getMobile()));
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
        userRepository.save(registeredUser);

        // Generate JWT token
        String jwt = generateJWTToken(registerRequestDTO.getEmail(), registerRequestDTO.getPassword());
        registeredUser.setToken(jwt);

        log.debug("User JWT: {}", jwt);

        // save the new user to the database
        registeredUser = userRepository.save(registeredUser);

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
    public Object getCurrentUser() throws EntityNotFoundException {
        User currentUser = null;
        Validator currentValidator = null;

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() != null) {
            currentUser = userRepository.findFirstByEmailOrMobile(userPrincipal.getUsername(), userPrincipal.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));
            log.debug("Current user: {}", currentUser.toDTO());
            return currentUser.toDTO();
        } else if (userPrincipal.getValidator() != null) {
            return validatorService.getValidatorEntityByName(userPrincipal.getUsername());
        } else {
            log.error("User not found!");
            throw new EntityNotFoundException("User not found");
        }
    }

    /**
     * This method is used to update the user information.
     * The user can only update his/her first name and last name.
     * The admin can update the first name, last name, email, mobile, isActive and role of the user.
     *
     * @param firstName         the new first name of the user
     * @param lastName          the new last name of the user
     * @param email             the new email of the user
     * @param profilePicture    the new profile picture of the user
     * @return the updated user {@link UserDTO}
     * @throws EntityNotFoundException if the user does not exist
     */
    @Override
    public UserDTO updateMyProfile(String firstName, String lastName, String email, MultipartFile profilePicture) throws EntityNotFoundException, IOException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            log.error("User not found!");
            throw new EntityNotFoundException("User not found");
        }

        User updatedUser = userRepository.findFirstByEmailOrMobile(userPrincipal.getUsername(), userPrincipal.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        log.debug("User Mobile Number: {}", updatedUser.getMobile());

        if (firstName != null && !firstName.isEmpty()) {
            updatedUser.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            updatedUser.setLastName(lastName);
        }

        if (email != null && !email.isEmpty()) {
            if (!updatedUser.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
                log.error("User with email: {} already exists", email);
                throw new BadRequestException(String.format("User with email: %s already exists", email));
            }
            updatedUser.setEmail(email);
        }

        // update the profile picture
        if (profilePicture != null) {
            log.debug("Profile picture is not null");
            // get the current profile picture
            String currentProfilePicture = updatedUser.getProfilePicture();
            log.debug("Current profile picture: {}", currentProfilePicture);
            // save the new profile picture
            String profilePictureUrl = new FileUtils().saveFile(Collections.singletonList(profilePicture), updatedUser.getId());
            // if the profile picture is updated successfully, add the new profile picture to the user
            if (profilePictureUrl != null) {
                updatedUser.setProfilePicture(profilePictureUrl);
                log.debug("Profile picture is updated successfully: {}", profilePictureUrl);
            }
            // if the user has an old profile picture, delete it
            if (currentProfilePicture != null) {
                log.debug("Delete the old profile picture: {}", currentProfilePicture);
                new FileUtils().deleteFile(currentProfilePicture);
            }
        } else {
            log.debug("Profile picture is null");
        }

        // update the user
        updatedUser = userRepository.save(updatedUser);
        log.debug("User updated successfully: {}", updatedUser.toDTO());

        return updatedUser.toDTO();
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
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("No user with id %s found", id)));
        log.debug("User is found: {}", user.toDTO());
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
        if (userRepository.findFirstByEmailOrMobile(registerRequestDTO.getEmail(), registerRequestDTO.getMobile()).isPresent()) {
            throw new BadRequestException(String.format("User with email %s or mobile %s already exists!", registerRequestDTO.getEmail(), registerRequestDTO.getMobile()));
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
     * @param id                the id of the user to be updated
     * @param firstName         the new first name of the user
     * @param lastName          the new last name of the user
     * @param email             the new email of the user
     * @param mobile            the new mobile of the user
     * @param isActive          the new isActive of the user
     * @param role              the new role of the user
     * @param profilePicture    the new profile picture of the user
     * @return the updated user {@link UserDTO}
     * @throws EntityNotFoundException if the user does not exist
     */
    @Override
    public UserDTO updateUser(String id, String firstName, String lastName, String email, String mobile,
                              Boolean isActive, UserRole role, MultipartFile profilePicture) throws EntityNotFoundException, IOException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user with given id %s found!", id)));

        if (firstName != null && !firstName.isEmpty()) {
            updatedUser.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            updatedUser.setLastName(lastName);
        }

        if (email != null && !email.isEmpty()) {
            updatedUser.setEmail(email);
        }

        // only admin can update the role, email, isActive and mobile
        if (userPrincipal.getUser().getRole() == UserRole.ROLE_ADMIN) {
            if (role != null) {
                updatedUser.setRole(role);
            }

            if (mobile != null && !mobile.isEmpty()) {
                updatedUser.setMobile(mobile);
            }

            if (isActive != null) {
                updatedUser.setIsActive(isActive);
            }
        }

        // update the profile picture
        if (profilePicture != null) {
            // get the current profile picture
            String currentProfilePicture = updatedUser.getProfilePicture();
            // save the new profile picture
            String profilePictureUrl = new FileUtils().saveFile(Collections.singletonList(profilePicture), updatedUser.getId());
            // if the profile picture is updated successfully, add the new profile picture to the user
            if (profilePictureUrl != null) {
                updatedUser.setProfilePicture(profilePictureUrl);
            }
            // if the user has an old profile picture, delete it
            if (currentProfilePicture != null) {
                new FileUtils().deleteFile(currentProfilePicture);
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
            throw new EntityNotFoundException(String.format("No user with id %s found", id));
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
     * This method is used check if the user exists by given email or mobile
     *
     * @param email the user email to be searched
     * @param mobile the user mobile to be searched
     * @return true if the user exists, false otherwise
     * @throws BadRequestException if the email or mobile is not provided
     */
    @Override
    public Boolean userExists(String email, String mobile) throws EntityNotFoundException {
        return userRepository.findFirstByEmailOrMobile(email, mobile).isPresent();
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user with given id %s found!", id)));
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user with given mobile number %s found!", generateOTPDTO.getMobile())));

        // generate a random 5 digit number
        Random rand = new Random();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 5);

        String otp = String.format("%05d", rand.nextInt(100000));

        if (PropertyLoader.getEnv().equals("DEVELOPMENT")) {
            user.setOtpCode(passwordEncoder.encode("00000"));
        } else {
            user.setOtpCode(passwordEncoder.encode(otp));
            SMSSender smsSender = new SMSSenderCequens();
            smsSender.sendSMS(generateOTPDTO.getMobile(), "Your OTP is " + otp);
        }

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

    /**
     * This method is used to return the users vendors
     *
     * @return list of vendors that user has promotions for
     * @throws EntityNotFoundException if no user is authenticated
     */
    @Override
    public List<VendorDTO> getVendors() throws EntityNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        User user = userRepository.findById(userPrincipal.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user with given id %s found!", userPrincipal.getUser().getId())));

        return user.getPromotions().stream()
                .map(Promotion::getVendor)
                .distinct()
                .map(vendor -> VendorDTO.builder()
                        .id(vendor.getId())
                        .name(vendor.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
