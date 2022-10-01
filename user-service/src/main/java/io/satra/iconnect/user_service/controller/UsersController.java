package io.satra.iconnect.user_service.controller;


import io.satra.iconnect.user_service.dto.RegisterRequestDTO;
import io.satra.iconnect.user_service.dto.ResponseDTO;
import io.satra.iconnect.user_service.dto.UpdatePasswordDTO;
import io.satra.iconnect.user_service.dto.UpdateProfileRequestDTO;
import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.security.UserPrincipal;
import io.satra.iconnect.user_service.service.UserService;
import io.satra.iconnect.user_service.utils.TimeUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Api(value = "Authentication Back - End Services")
@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Page<User> getAllUsers(Pageable page) {
        Page<User> users = userService.findAllUsers(page);
        return users;
    }

    @PostMapping
    public ResponseEntity<?> saveNewUser(@RequestBody RegisterRequestDTO user) {
        User newUser = userService.save(user);
        return ResponseEntity.ok(new ResponseDTO("Success", true, user));

    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody UpdateProfileRequestDTO u) {
        User user = userService.updateUser(TimeUtils.getUserId(), u);
        return ResponseEntity.ok(new ResponseDTO("Success", true, user));

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findUserById(@PathVariable(name = "id") Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(new ResponseDTO("Success", true, user));

    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?>  searchForUser(@RequestParam(required = true) String username, Pageable page) {
        Page<User> users = userService.searchUsers(username, page);
        return ResponseEntity.ok(new ResponseDTO("Success", true, users));
    }

    @RequestMapping(value = "/loggedinUser", method = RequestMethod.GET)
    public ResponseEntity<?> loggedinUser() {

        UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        //Users user = usersService.findById(Utils.getUserId());
        return ResponseEntity.ok(new ResponseDTO("Success", true, userDetails));

    }


    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDTO u) {
        User user = userService.updatePassword(TimeUtils.getUserId(), u);
        return ResponseEntity.ok(new ResponseDTO("Success", true, user));

    }

}
