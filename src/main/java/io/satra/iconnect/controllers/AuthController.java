package io.satra.iconnect.controllers;

import com.google.gson.Gson;
import io.satra.iconnect.dto.MerchantDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.*;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.merchant.MerchantService;
import io.satra.iconnect.service.user.UserService;
import io.satra.iconnect.service.validator.ValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ValidatorService validatorService;
    private final MerchantService merchantService;

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
        log.debug("API ---> (/api/auth/login) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".login()");
        log.debug("Request body: {}", loginRequestDTO);
        return ResponseEntity.ok(userService.loginUser(loginRequestDTO));
    }

    /**
     * This endpoint logs out a user from the application.
     * @return the logged-out flag {@link ResponseDTO}
     */
    @DeleteMapping("/logout")
    @Operation(summary = "Logout a user from the application")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> logout() {
        log.debug("API ---> (/api/auth/logout) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".logout()");
        boolean isLoggedOut = userService.logout();
        return ResponseEntity.ok(
                ResponseDTO.builder()
                .message(isLoggedOut ? "User logged out successfully":"User failed to logout")
                .success(isLoggedOut)
                .build()
        );
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
        log.debug("API ---> (/api/auth/register) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".register()");
        log.debug("Request body: {}", registerRequestDTO);
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
    public ResponseEntity<?> sendOTP(@RequestBody GenerateOTPDTO generateOTPDTO) throws EntityNotFoundException {
        log.debug("API ---> (/api/auth/otp/generate) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".sendOTP()");
        log.debug("Request body: {}", generateOTPDTO);
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
        log.debug("API ---> (/api/auth/login/validator) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".loginValidator()");
        log.debug("Request body: {}", validatorLoginRequestDTO);
        return ResponseEntity.ok(validatorService.loginValidator(validatorLoginRequestDTO));
    }

    /**
     * This endpoint is used to register a new merchant.
     *
     * @param merchantTxt the merchant information to register
     * @param logo the merchant logo
     * @return the registered merchant {@link JwtResponseDTO}
     * @throws BadRequestException if the merchant is not valid or already exists
     */
    @PostMapping("/register/merchant")
    @Operation(summary = "Register a new merchant to the application")
    public ResponseEntity<?> registerMerchant(@RequestPart("merchant") String merchantTxt, @RequestPart("logo") MultipartFile logo)
            throws BadRequestException, IOException {
        log.debug("API ---> (/api/auth/register/merchant) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".registerMerchant()");
        log.debug("Request body: {}", merchantTxt);

        // convert the merchant string to a merchant object using gson
        Gson gson = new Gson();
        MerchantRequestDTO merchantReqDTO = gson.fromJson(merchantTxt, MerchantRequestDTO.class);

        // check if the merchant is valid
        if (merchantReqDTO == null) {
            throw new BadRequestException("Merchant is required");
        }
        if (merchantReqDTO.getName() == null || merchantReqDTO.getName().isEmpty()) {
            throw new BadRequestException("Merchant name is required");
        }
        if (merchantReqDTO.getAdminFirstName() == null || merchantReqDTO.getAdminFirstName().isEmpty()) {
            throw new BadRequestException("Merchant admin first name is required");
        }
        if (merchantReqDTO.getAdminLastName() == null || merchantReqDTO.getAdminLastName().isEmpty()) {
            throw new BadRequestException("Merchant admin last name is required");
        }
        if (merchantReqDTO.getAdminEmail() == null || merchantReqDTO.getAdminEmail().isEmpty()) {
            throw new BadRequestException("Merchant admin email is required");
        }
        if (merchantReqDTO.getPassword() == null || merchantReqDTO.getPassword().isEmpty()) {
            throw new BadRequestException("Merchant password is required");
        }
        if (merchantReqDTO.getMobile() == null || merchantReqDTO.getMobile().isEmpty()) {
            throw new BadRequestException("Merchant mobile number is required");
        }
        if (merchantReqDTO.getCity() == null || merchantReqDTO.getCity().isEmpty()) {
            throw new BadRequestException("Merchant city is required");
        }
        if (merchantReqDTO.getCountry() == null || merchantReqDTO.getCountry().isEmpty()) {
            throw new BadRequestException("Merchant country is required");
        }
        if (logo == null) {
            throw new BadRequestException("Merchant logo is required");
        }

        log.debug("Merchant logo size: {} {}", logo.getSize() / 1024 > 1024 ? logo.getSize() / 1024 / 1024 : logo.getSize() / 1024, logo.getSize() / 1024 > 1024 ? "MB" : "KB");
        log.debug("Merchant logo type: {}", logo.getContentType());

        // convert the merchant object to a merchant entity
        Merchant merchant = new Merchant();
        merchant.setName(merchantReqDTO.getName());
        merchant.setAbbreviation(merchantReqDTO.getAbbreviation());
        merchant.setAdminFirstName(merchantReqDTO.getAdminFirstName());
        merchant.setAdminLastName(merchantReqDTO.getAdminLastName());
        merchant.setAdminEmail(merchantReqDTO.getAdminEmail());
        merchant.setPassword(merchantReqDTO.getPassword());
        merchant.setMobile(merchantReqDTO.getMobile());
        merchant.setFirstAddress(merchantReqDTO.getFirstAddress());
        merchant.setSecondAddress(merchantReqDTO.getSecondAddress());
        merchant.setCity(merchantReqDTO.getCity());
        merchant.setState(merchantReqDTO.getState());
        merchant.setCountry(merchantReqDTO.getCountry());

        JwtResponseDTO registeredMerchant = merchantService.register(merchant, logo);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users/" + registeredMerchant.getMerchant().getId()).toUriString());
        return ResponseEntity.created(uri).body(registeredMerchant);
    }

    /**
     * This endpoint is used to log in a merchant.
     *
     * @param loginRequestDTO the merchant information to log in
     *                        email and password
     * @return the logged-in merchant with a JWT token {@link JwtResponseDTO}
     * */
    @PostMapping("/login/merchant")
    @Operation(summary = "Login a merchant to the application")
    public ResponseEntity<?> loginMerchant(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.debug("API ---> (/api/auth/login/merchant) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".loginMerchant()");
        log.debug("Request body: {}", loginRequestDTO);
        return ResponseEntity.ok(merchantService.loginMerchant(loginRequestDTO));
    }
}
