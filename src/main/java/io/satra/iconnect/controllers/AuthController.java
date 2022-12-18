package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.GenerateOTPDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.request.ValidatorLoginRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.user.UserService;
import io.satra.iconnect.service.validator.ValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ValidatorService validatorService;

    /**
     * This endpoint logs in a user to the application.
     *
     * @param loginRequestDTO the user information used for login
     *                         username and password
     * @return the logged-in user with a JWT token {@link JwtResponseDTO}
     */
    @PostMapping("/login")
    @Operation(summary = "Login a user to the application. Can login with email and password or mobile number and OTP code")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(userService.loginUser(loginRequestDTO));
    }

    /**
     * This endpoint is used to register a new user.
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link UserDTO}
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user to the application")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) throws BadRequestException {
        log.info("Registering user with email: {} and mobile: {}", registerRequestDTO.getEmail(), registerRequestDTO.getMobile());
        JwtResponseDTO registeredUser = userService.register(registerRequestDTO);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users/" + registeredUser.getUser().getId()).toUriString());
        return ResponseEntity.created(uri).body(registeredUser);
    }

    /**
     * This endpoint is used to send an OTP to the user's.
     *
     * @param generateOTPDTO the user mobile number to send OTP
     * @return {@link ResponseDTO} with the status of the request
     * @throws EntityNotFoundException if no user is authenticated
     */
    @PostMapping("/otp/generate")
    @Operation(summary = "Generate and send an OTP to the user's mobile number")
    public ResponseEntity<?> sendOTP(@RequestBody GenerateOTPDTO generateOTPDTO) throws EntityNotFoundException{
        return ResponseEntity.ok(userService.sendOTP(generateOTPDTO));
    }

    /**
     * This endpoint takes the validator key and returns the associated vendor.
     *
     * @param validatorLoginRequestDTO the validator object that contains the key and customer id
     * @return the validator with the associated vendor
     * @throws EntityNotFoundException if no validator is found
     */
    @PostMapping("/login/validator")
    @Operation(summary = "Authenticate a vendor with the validator key")
    public ResponseEntity<?> loginValidator(@Valid @RequestBody ValidatorLoginRequestDTO validatorLoginRequestDTO) throws EntityNotFoundException {
        return ResponseEntity.ok(validatorService.loginValidator(validatorLoginRequestDTO));
    }
}
