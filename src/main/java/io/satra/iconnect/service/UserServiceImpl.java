package io.satra.iconnect.service;

import com.google.gson.Gson;
import io.satra.iconnect.dto.QRCodeDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.repository.UserRepository;
import io.satra.iconnect.security.JWTUtils;
import io.satra.iconnect.utils.EncodingUtils;
import io.satra.iconnect.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
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
       log.info("Logging in user with email: {}, and password: {}", loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());
         User user = userRepository.findByEmailOrMobile(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getEmailOrMobile())
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        // Generate JWT token
        String jwt = generateJWTToken(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());

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
                .role(UserRole.USER)
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .build();

        // generate QR code
        registeredUser.setQrCode(generateQRCode(registeredUser));

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
    private String generateQRCode(User user) {
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
