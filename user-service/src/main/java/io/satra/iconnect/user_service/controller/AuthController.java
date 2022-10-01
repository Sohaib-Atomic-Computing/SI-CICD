package io.satra.iconnect.user_service.controller;


import io.satra.iconnect.user_service.dto.GenerateOTPDTO;
import io.satra.iconnect.user_service.dto.JwtResponseDTO;
import io.satra.iconnect.user_service.dto.LoginRequestDTO;
import io.satra.iconnect.user_service.dto.RefreshTokenDTO;
import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.ResponseDTO;
import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.entity.UserRefreshToken;
import io.satra.iconnect.user_service.entity.enums.ServiceType;
import io.satra.iconnect.user_service.exception.generic.BadRequestException;
import io.satra.iconnect.user_service.exception.generic.ForbiddenRequestException;
import io.satra.iconnect.user_service.repository.UserRefreshTokenRepository;
import io.satra.iconnect.user_service.repository.UserRepository;
import io.satra.iconnect.user_service.security.jwt.JwtProvider;
import io.satra.iconnect.user_service.service.UserService;
import io.swagger.annotations.Api;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Api(value = "Authentication Back - End Services")
@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
public class AuthController {

    Logger  logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRefreshTokenRepository userRefreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByEmailOrPhoneNumber(loginRequestDTO.getPhoneNumberOrEmail(), loginRequestDTO.getPhoneNumberOrEmail())
            .orElseThrow(()
                -> new ForbiddenRequestException("Username or Phone Number Not Found   : " + loginRequestDTO.getPhoneNumberOrEmail()
            ));
        if ((loginRequestDTO.getServiceType() == ServiceType.LOGIN && user.isActive() == Boolean.TRUE)
            | (loginRequestDTO.getServiceType() == ServiceType.OTP_VERIFY && user.isActive() == Boolean.FALSE)) {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getPhoneNumberOrEmail(), loginRequestDTO.getPasswordOrCode()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            JwtResponseDTO jwt = jwtProvider.generateJwtToken(authentication);

            String refreshToken = createRefreshToken(user);

            if (loginRequestDTO.getServiceType() == ServiceType.OTP_VERIFY) {
                user.setActive(Boolean.TRUE);
                user.setPhoneVerified(Boolean.TRUE);
                userRepository.save(user);
            }
            jwt.setRefreshToken(refreshToken);

            logger.log(Level.INFO, "User logged in successfully " + user.getUserUniqueId());
            return ResponseEntity.ok(new ResponseDTO("Success", true, jwt));
        }
        else {
            throw new BadRequestException("Invalid Operation  : " + loginRequestDTO.getPhoneNumberOrEmail());
        }

    }

    private String createRefreshToken(User user) {
        String token = RandomStringUtils.randomAlphanumeric(128);
        userRefreshTokenRepository.save(new UserRefreshToken(token, user));
        return token;

    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> save(@RequestBody RegisterRequestDTO user, HttpServletRequest request) throws MessagingException {
        User newUser = userService.save(user);

        logger.log(Level.INFO, "The user " + newUser.getFullName() + " has been registered successfully" + " " + newUser.getResetToken());

        return ResponseEntity.ok(new ResponseDTO("Success", true, newUser));

    }

    @GetMapping(value = "/logout")
    public void logoutUser(String refreshToken) {
        userRefreshTokenRepository.findByToken(refreshToken)
                .ifPresent(userRefreshTokenRepository::delete);
        logger.log(Level.INFO,"Logged out successfully");
    }

    @PostMapping(value = "/token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenDTO refreshToken) {

        Optional<JwtResponseDTO> jwtResponse = userRefreshTokenRepository.findByToken(refreshToken.getRefreshToken())
            .map(userRefreshToken -> (jwtProvider.generateJwtToken(userRefreshToken.getUser().getEmail(), refreshToken.getRefreshToken())));

        if (jwtResponse == null) {
            throw new ForbiddenRequestException("Incorrect Token");
        } else {
            return ResponseEntity.ok(new ResponseDTO("Success", true, jwtResponse));
        }
    }

        @PostMapping(value = "/CheckUserAndGenerateOTP")
        public ResponseEntity<?> generateOTP(@RequestBody GenerateOTPDTO  otp) {
            Boolean isOTPgenerated = userService.generateOTPByPhoneNumber(otp.getPhoneNumber());
            return ResponseEntity.ok(new ResponseDTO("OTP Generated", true, isOTPgenerated));
        }


    }


