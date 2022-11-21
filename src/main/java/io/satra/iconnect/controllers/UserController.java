package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.request.UpdateProfileRequestDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

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

    /**
     * This endpoint obtains a user with the given id
     *
     * @param id the id of the user to be obtained
     * @return the obtained {@link UserDTO}
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> findUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    /**
     * This endpoint creates a new admin user.
     *
     * @param registerRequestDTO the user information to register
     * @return the obtained {@link UserDTO}
     */
    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addAdminUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        UserDTO user = userService.addAdminUser(registerRequestDTO);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users/" + user.getId()).toUriString());
        return ResponseEntity.created(uri).body(
                ResponseDTO.builder()
                        .message("Admin created successfully")
                        .success(true)
                        .data(user)
                        .build()
        );
    }

    /**
     * This endpoint updates the profile information of a given user
     *
     * @param id                      the id of the user to be updated
     * @param updateProfileRequestDTO the information for updating the user profile
     * @return the updated {@link UserDTO}
     * @throws EntityNotFoundException if no user with given id is found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO)
            throws EntityNotFoundException {
        UserDTO user = userService.updateUser(id, updateProfileRequestDTO);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("User updated successfully")
                        .success(true)
                        .data(user)
                        .build()
        );
    }

    /**
     * This endpoint deletes a user with given id
     *
     * @param id the id of the user to be deleted
     * @return success message if the user is deleted successfully {@link ResponseDTO}
     * @throws EntityNotFoundException if no user with given id is found
     */
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) throws EntityNotFoundException{
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("User deleted successfully")
                .success(true)
                .build());
    }

    /**
     * This endpoint obtains all existing users with pagination
     *
     * @param page the current page of user to be obtained
     * @return a {@link Page} of {@link UserDTO}
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable page) {
        return ResponseEntity.ok(userService.findAllUsers(page));
    }
}
