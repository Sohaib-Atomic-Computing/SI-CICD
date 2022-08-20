package com.iconnect.backend.controllers;


import com.iconnect.backend.dtos.*;
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

    @Value("${app.url}")
    private String appUrl;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRefreshTokenRepository userRefreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Users user = userRepository.findByEmailOrUsername(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail())
                .orElseThrow(()
                        -> new ForbiddenRequestException("Username or Email Not Active   : " + loginRequest.getUsernameOrEmail()
                ));

        JwtResponse jwt = jwtProvider.generateJwtToken(authentication);

        String refreshToken = createRefreshToken(user);
        jwt.setRefreshtoken(refreshToken);
        logger.log(Level.INFO,"User logged in successfully " +user.getUsername());
        return ResponseEntity.ok(jwt);
    }

    private String createRefreshToken(Users user) {
        String token = RandomStringUtils.randomAlphanumeric(128);
        userRefreshTokenRepository.save(new UserRefreshToken(token, user));
        return token;

    }

    @PostMapping(value = "/register")
    public ResponseEntity<Users> save(@RequestBody RegisterRequest user , HttpServletRequest request) throws MessagingException {
        Users newUser = usersService.save(user);

        logger.log(Level.INFO,"The user "+newUser.getUsername()+" has been registered successfully" + " " + newUser.getResetToken());

        return ResponseEntity.ok(newUser);

    }

    @GetMapping(value = "/logout")
    public void logoutUser(String refreshToken) {
        userRefreshTokenRepository.findByToken(refreshToken)
                .ifPresent(userRefreshTokenRepository::delete);
        logger.log(Level.INFO,"Logged out successfully");
    }

    @PostMapping(value = "/token")
    public Optional<JwtResponse> refreshAccessToken(@RequestBody RefreshTokenDTO refreshToken) {
        return userRefreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                .map(userRefreshToken -> (jwtProvider.generateJwtToken(userRefreshToken.getUser().getEmail(), refreshToken.getRefreshToken())));

    }

}