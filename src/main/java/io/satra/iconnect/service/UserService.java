package io.satra.iconnect.service;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.exception.generic.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    /**
     * Register a new user
     *
     * @param registerRequestDTO the user information to register
     * @return the registered user {@link UserDTO}
     * @throws BadRequestException if the user already exists
     */
    UserDTO register(RegisterRequestDTO registerRequestDTO) throws BadRequestException;
}
