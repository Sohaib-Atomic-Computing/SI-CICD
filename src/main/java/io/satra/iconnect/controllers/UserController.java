package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.RegisterRequestDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_VALIDATOR')")
    @Operation(summary = "Get the current authenticated user information")
    public ResponseEntity<?> getUserInfo() throws EntityNotFoundException {
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
    @Operation(summary = "Get a user by id")
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
    @Operation(summary = "Create a new admin user")
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
     * @param id                the id of the user to be updated
     * @param firstName         the new first name of the user
     * @param lastName          the new last name of the user
     * @param email             the new email of the user
     * @param mobile            the new mobile of the user
     * @param isActive          the new active status of the user
     * @param role              the new role of the user
     * @param profilePicture    the new profile picture of the user
     * @return the updated {@link UserDTO}
     * @throws EntityNotFoundException if no user with given id is found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update the profile information of a given user")
    public ResponseEntity<?> updateUser(@PathVariable String id,
                                        @RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String mobile,
                                        @RequestParam(required = false) Boolean isActive,
                                        @RequestParam(required = false) UserRole role,
                                        @RequestParam(required = false) MultipartFile profilePicture)
            throws EntityNotFoundException, IOException {
        UserDTO user = userService.updateUser(id, firstName, lastName, email, mobile, isActive, role, profilePicture);
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
    @Operation(summary = "Delete a user by id")
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
     * @param email the email of the user to be obtained
     * @param mobile the mobile of the user to be obtained
     * @param firstName the first name of the user to be obtained
     * @param lastName the last name of the user to be obtained
     * @param page the current page of user to be obtained
     * @return a {@link Page} of {@link UserDTO}
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all users with pagination and filters")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Pageable page) {
        return ResponseEntity.ok(userService.findAllUsers(email, mobile, firstName, lastName,  page));
    }

    /**
     * This endpoint is used to obtain the user vendors
     *
     * @return a list of vendors {@link VendorDTO}
     * @throws EntityNotFoundException if no user is authenticated
     */
    @GetMapping("/vendors")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get vendors list that the current user have promotions for")
    public ResponseEntity<?> getVendors() throws EntityNotFoundException {
        List<VendorDTO> vendors = userService.getVendors();
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("User has promotions in the following vendors")
                .success(true)
                .data(vendors)
                .build());
    }

    /**
     * This endpoint check if the given email or mobile is already registered
     *
     * @param email the email of the user to be obtained
     * @param mobile the mobile of the user to be obtained
     * @return success message if the user exists {@link ResponseDTO} and failure message if the user does not exist
     * @throws EntityNotFoundException if no user with given email or mobile is found
     */
    @GetMapping("/check")
    @Operation(summary = "Check if the given email or mobile is already registered")
    public ResponseEntity<?> userExists(@RequestParam(required = false) String email, @RequestParam(required = false) String mobile)
            throws EntityNotFoundException {
        if (email == null && mobile == null) {
            throw new BadRequestException("Email or mobile is required");
        }
        boolean exists = userService.userExists(email, mobile);
        return ResponseEntity.ok(ResponseDTO.builder()
                .message(exists ? "User exists" : "User does not exist")
                .success(exists)
                .build());
    }
}
