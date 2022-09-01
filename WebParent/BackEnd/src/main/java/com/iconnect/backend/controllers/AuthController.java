package com.iconnect.backend.controllers;


import com.iconnect.backend.dtos.*;
import com.iconnect.backend.exception.BadRequestException;
import com.iconnect.backend.exception.ForbiddenRequestException;
import com.iconnect.backend.exception.RecordNotFoundException;
import com.iconnect.backend.model.UserRefreshToken;
import com.iconnect.backend.model.Users;
import com.iconnect.backend.repository.UserRefreshTokenRepository;
import com.iconnect.backend.repository.UsersRepository;
import com.iconnect.backend.security.jwt.JwtProvider;
import com.iconnect.backend.services.UsersService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private UsersService usersService;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRefreshTokenRepository userRefreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumberOrEmail(), loginRequest.getPasswordOrCode()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Users user = userRepository.findByEmailOrPhoneNumber(loginRequest.getPhoneNumberOrEmail(), loginRequest.getPhoneNumberOrEmail())
                .orElseThrow(()
                        -> new ForbiddenRequestException("Username or Phone Number Not Found   : " + loginRequest.getPhoneNumberOrEmail()
                ));

        JwtResponse jwt = jwtProvider.generateJwtToken(authentication);

        String refreshToken = createRefreshToken(user);
      //  user.setOTPCode(null);
      //  userRepository.save(user);
        jwt.setRefreshtoken(refreshToken);

        logger.log(Level.INFO,"User logged in successfully " + user.getUserUniqueId());
        return ResponseEntity.ok(new Response("Success",true,jwt));
    }

    private String createRefreshToken(Users user) {
        String token = RandomStringUtils.randomAlphanumeric(128);
        userRefreshTokenRepository.save(new UserRefreshToken(token, user));
        return token;

    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> save(@RequestBody RegisterRequest user , HttpServletRequest request) throws MessagingException {
        Users newUser = usersService.save(user);

        logger.log(Level.INFO,"The user "+newUser.getFullName()+" has been registered successfully" + " " + newUser.getResetToken());

        return ResponseEntity.ok(new Response("Success",true,newUser));

    }

    @GetMapping(value = "/logout")
    public void logoutUser(String refreshToken) {
        userRefreshTokenRepository.findByToken(refreshToken)
                .ifPresent(userRefreshTokenRepository::delete);
        logger.log(Level.INFO,"Logged out successfully");
    }

    @PostMapping(value = "/token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenDTO refreshToken) {


        Optional<JwtResponse> jwtResponse = userRefreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                .map(userRefreshToken -> (jwtProvider.generateJwtToken(userRefreshToken.getUser().getEmail(), refreshToken.getRefreshToken())));

        if (jwtResponse == null) {
            throw new ForbiddenRequestException("Incorrect Token");
        } else {
            return ResponseEntity.ok(new Response("Success", true, jwtResponse));
        }
    }

        @PostMapping(value = "/CheckUserAndGenerateOTP")
        public ResponseEntity<?> generateOTP(@RequestBody GenerateOTPDTO  otp)
        {
            Boolean isOTPgenerated = usersService.generateOTP(otp.getPhoneNumber());
            return ResponseEntity.ok(new Response("OTP Generated",true,isOTPgenerated));
        }


    }


