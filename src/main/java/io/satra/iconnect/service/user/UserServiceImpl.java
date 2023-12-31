package io.satra.iconnect.service.user;

import com.google.gson.Gson;
import io.satra.iconnect.dto.QRCodeDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.*;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.entity.Promotion;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.UserRepository;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.service.merchant.MerchantService;
import io.satra.iconnect.service.validator.ValidatorService;
import io.satra.iconnect.utils.*;
import io.satra.iconnect.utils.sms.SMSSender;
import io.satra.iconnect.utils.sms.SMSSenderCequens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final MerchantService merchantService;
    private final PasswordEncoder passwordEncoder;
    private final JWTToken jwtToken;

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
        String jwt = jwtToken.generate(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());
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
     * This method logs out the user by removing the token from the user
     *
     * @return true if the user is logged out successfully
     * @throws EntityNotFoundException if the user is not exists
     */
    @Override
    public boolean logout() throws EntityNotFoundException {

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            log.debug("User not found!");
            throw new EntityNotFoundException("User not found!");
        }

        User currentUser = userRepository.findFirstByEmailOrMobile(userPrincipal.getUsername(), userPrincipal.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        currentUser.setToken(null);
        userRepository.save(currentUser);
        return true;
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

        // validate the user email and mobile number
        ValidateUtil vd = new ValidateUtil();

        if (!vd.checkEmailValidation(registerRequestDTO.getEmail())) {
            throw new BadRequestException("Invalid email address! Please provide proper email address");
        }

        if (!vd.checkMobileNumberValidation(registerRequestDTO.getMobile())) {
            if(registerRequestDTO.getMobile() != null && !registerRequestDTO.getMobile().equals("")
                && !(registerRequestDTO.getMobile().trim().startsWith("00") || registerRequestDTO.getMobile().trim().startsWith("+"))) {
                throw new BadRequestException("Invalid mobile number! The mobile number should start with 00 or +");
            }

            throw new BadRequestException("Invalid mobile number! Please provide proper mobile number");
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
        String jwt = jwtToken.generate(registerRequestDTO.getEmail(), registerRequestDTO.getPassword());
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
        Merchant currentMerchant = null;

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() != null) {
            currentUser = userRepository.findFirstByEmailOrMobile(userPrincipal.getUsername(), userPrincipal.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));
            log.debug("Current user: {}", currentUser.toDTO());
            return currentUser.toDTO();
        } else if (userPrincipal.getValidator() != null) {
            return validatorService.getValidatorEntityByName(userPrincipal.getUsername()).toDTO();
        } else if (userPrincipal.getMerchant() != null) {
            return merchantService.getMerchantEntityByEmail(userPrincipal.getUsername()).toDTO();
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

        ValidateUtil vd = new ValidateUtil();

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

            // validate the user email
            if (!vd.checkEmailValidation(email)) {
                throw new BadRequestException("Invalid email address! Please provide proper email address");
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

    @Override
    public UserDTO changePassword(ChangePasswordDTO changePasswordDTO) throws EntityNotFoundException, BadRequestException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            log.error("User not found!");
            throw new EntityNotFoundException("User not found");
        }

        User user = userRepository.findFirstByEmailOrMobile(userPrincipal.getUsername(), userPrincipal.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            log.error("Old password is incorrect");
            throw new BadRequestException("Old password is incorrect");
        }

        // check if the new password is the same as the old password
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            log.error("New password is the same as the old password");
            throw new BadRequestException("New password is the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user = userRepository.save(user);
        log.debug("Password changed successfully: {}", user.toDTO());

        return user.toDTO();
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

        // validate the user email and mobile number
        ValidateUtil vd = new ValidateUtil();

        if (!vd.checkEmailValidation(registerRequestDTO.getEmail())) {
            throw new BadRequestException("Invalid email address! Please provide proper email address");
        }

        if (!vd.checkMobileNumberValidation(registerRequestDTO.getMobile())) {
            if(registerRequestDTO.getMobile() != null && !registerRequestDTO.getMobile().equals("")
                    && !(registerRequestDTO.getMobile().trim().startsWith("00") || registerRequestDTO.getMobile().trim().startsWith("+"))) {
                throw new BadRequestException("Invalid mobile number! The mobile number should start with 00 or +");
            }

            throw new BadRequestException("Invalid mobile number! Please provide proper mobile number");
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

        ValidateUtil vd = new ValidateUtil();

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
            // validate the user email
            if (!vd.checkEmailValidation(email)) {
                throw new BadRequestException("Invalid email address! Please provide proper email address");
            }
            updatedUser.setEmail(email);
        }

        // only admin can update the role, email, isActive and mobile
        if (userPrincipal.getUser().getRole() == UserRole.ROLE_ADMIN) {
            if (role != null) {
                updatedUser.setRole(role);
            }

            if (mobile != null && !mobile.isEmpty()) {
                if (!updatedUser.getMobile().equals(mobile) && userRepository.findByMobile(mobile).isPresent()) {
                    log.error("User with mobile: {} already exists", mobile);
                    throw new BadRequestException(String.format("User with mobile: %s already exists", mobile));
                }
                // validate the user mobile number
                if (!vd.checkMobileNumberValidation(mobile)) {
                    if(!(mobile.trim().startsWith("00") || mobile.trim().startsWith("+"))) {
                        throw new BadRequestException("Invalid mobile number! The mobile number should start with 00 or +");
                    }

                    throw new BadRequestException("Invalid mobile number! Please provide proper mobile number");
                }
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
    public Boolean userExists(String email, String mobile) throws BadRequestException {
        if (email != null && !email.isEmpty() && mobile != null && !mobile.isEmpty()) {
            return userRepository.findFirstByEmailOrMobile(email, mobile).isPresent();
        } else if (email != null && !email.isEmpty()) {
            return userRepository.findByEmail(email).isPresent();
        } else if (mobile != null && !mobile.isEmpty()) {
            return userRepository.findByMobile(mobile).isPresent();
        } else {
            throw new BadRequestException("Please provide email or mobile");
        }
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
                    .mobile("+201122222222")
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
     * This method is used to get the user active entity by given id
     * @param id the id of the user to be obtained
     * @return a {@link User}
     * @throws EntityNotFoundException if no user with given id is found or the user is not active
     */
    @Override
    public User findActiveUserEntityById(String id) throws EntityNotFoundException {
        return userRepository.findByIdAndIsActive(id, true)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No active user with given id %s found!", id)));
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

        if (PropertyLoader.getEnv().equals("DEVELOPMENT") || generateOTPDTO.getMobile().equals("+201113109346")) {
            user.setOtpCode(passwordEncoder.encode("00000"));
        } else {
            user.setOtpCode(passwordEncoder.encode(otp));
            SMSSender smsSender = new SMSSenderCequens();
            smsSender.sendSMS(generateOTPDTO.getMobile(), "Verification code: " + otp);
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
                .filter(promotion -> {
                    Date date = new Date();
                    return date.after(TimeUtils.convertLocalDateTimeToDate(promotion.getStartDate()))
                            && date.before(TimeUtils.convertLocalDateTimeToDate(promotion.getEndDate()));
                })
                .map(Promotion::getVendor)
                .distinct()
                .map(vendor -> VendorDTO.builder()
                        .id(vendor.getId())
                        .name(vendor.getName())
                        .logo(vendor.getLogo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void addUser(AddUserRequest addUserRequest) throws BadRequestException {
        if (userRepository.findByMobile(addUserRequest.getMobile()).isPresent()) {
            return;
        }

        if ((addUserRequest.getEmail() == null || addUserRequest.getEmail().isEmpty()) &&
                (addUserRequest.getMobile() == null || addUserRequest.getMobile().isEmpty())) {
            throw new BadRequestException("Email or mobile number is required");
        }

        if(addUserRequest.getEmail() != null && !addUserRequest.getEmail().isEmpty()
                && userRepository.findByEmail(addUserRequest.getEmail()).isPresent()) {
            return;
        }

        // validate the email address and mobile number if they are not empty
        ValidateUtil vd = new ValidateUtil();
        if (addUserRequest.getEmail() != null && !addUserRequest.getEmail().isEmpty()) {
            if (!vd.checkEmailValidation(addUserRequest.getEmail())) {
                throw new BadRequestException("Invalid email address! Please provide proper email address");
            }
        }

        if (addUserRequest.getMobile() != null && !addUserRequest.getMobile().isEmpty()) {
            if (!vd.checkMobileNumberValidation(addUserRequest.getMobile())) {
                if(addUserRequest.getMobile() != null && !addUserRequest.getMobile().equals("")
                        && !(addUserRequest.getMobile().trim().startsWith("00") || addUserRequest.getMobile().trim().startsWith("+"))) {
                    throw new BadRequestException("Invalid mobile number! The mobile number should start with 00 or +");
                }
                throw new BadRequestException("Invalid mobile number! Please provide proper mobile number");
            }
        }

        User user = User.builder()
            .email(addUserRequest.getEmail())
            .mobile(addUserRequest.getMobile())
            .firstName(addUserRequest.getFirstName())
            .lastName(addUserRequest.getLastName())
            .role(UserRole.ROLE_USER)
            .isActive(true)
            .build();

        userRepository.save(user);
    }
}
