package io.satra.iconnect.service;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.exception.generic.BadRequestException;
import org.springframework.stereotype.Service;

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
     * Register a new user
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link JwtResponseDTO}
     * @throws BadRequestException if the user already exists
     */
    JwtResponseDTO register(RegisterRequestDTO registerRequestDTO) throws BadRequestException;
}
