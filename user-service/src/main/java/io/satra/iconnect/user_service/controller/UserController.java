package io.satra.iconnect.user_service.controller;


import io.satra.iconnect.user_service.dto.UpdatePasswordDTO;
import io.satra.iconnect.user_service.dto.UpdateProfileRequestDTO;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import io.satra.iconnect.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * This endpoint obtains all existing users with pagination
   *
   * @param page the current page of user to be obtained
   * @return a {@link Page} of {@link UserDTO}
   */
  @GetMapping
  public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable page) {
    return ResponseEntity.ok(userService.findAllUsers(page));
  }

  /**
   * This endpoint obtains a user with the given id
   *
   * @param id the id of the user to be obtained
   * @return the obtained {@link UserDTO}
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity<UserDTO> findUserById(@PathVariable String id) {
    return ResponseEntity.ok(userService.findUserById(id));
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
  public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO)
      throws EntityNotFoundException {
    return ResponseEntity.ok(userService.updateUser(id, updateProfileRequestDTO));
  }

  /**
   * This endpoint deletes a user with given id
   *
   * @param id the id of the user to be deleted
   */
  @DeleteMapping(value = "/{id}")
  public void deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
  }

  /**
   * This endpoint searches for users by phoneNumber
   *
   * @param phoneNumber the phoneNumber to be searched for
   * @param page        used for pagination
   * @return a {@link Page} of {@link UserDTO}
   */
  @GetMapping("/query/{phoneNumber}")
  public ResponseEntity<Page<UserDTO>> searchForUserByPhoneNumber(@PathVariable String phoneNumber, Pageable page) {
    Page<UserDTO> queryResult = userService.searchUsers(phoneNumber, page);
    return ResponseEntity.ok(queryResult);
  }

  /**
   * This endpoint updated the password of a given user
   *
   * @param id                the id of the user the password should be updated
   * @param updatePasswordDTO the information for password update
   * @return the updated {@link UserDTO}
   */
  @PutMapping("/{id}/password")
  public ResponseEntity<UserDTO> updatePasswordOfUser(@PathVariable String id, @RequestBody UpdatePasswordDTO updatePasswordDTO) {
    return ResponseEntity.ok(userService.updatePassword(id, updatePasswordDTO));
  }
}
