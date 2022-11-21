package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * This endpoint responds with the current authenticated user
     *
     * @return current authenticated {@link UserDTO}
     * @throws EntityNotFoundException if no user is authenticated
     */
    @GetMapping("/userinfo")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUserInfo() throws EntityNotFoundException {
        log.info("Getting user info");
        return ResponseEntity.ok(userService.getCurrentUser());
    }

}
