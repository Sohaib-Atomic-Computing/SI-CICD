package com.iconnect.backend.controllers;


import com.iconnect.backend.Utils.Utils;
import com.iconnect.backend.dtos.RegisterRequest;
import com.iconnect.backend.dtos.Response;
import com.iconnect.backend.dtos.UpdatePasswordDTO;
import com.iconnect.backend.dtos.UpdateProfileRequest;
import com.iconnect.backend.model.Users;
import com.iconnect.backend.security.services.UserPrinciple;
import com.iconnect.backend.services.UsersService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Api(value = "Authentication Back - End Services")
@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping
    public Page<Users> getAllUsers(Pageable page) {
        Page<Users> users = usersService.findAll(page);
        return users;
    }

    @PostMapping
    public ResponseEntity<?> saveNewUser(@RequestBody RegisterRequest user) {
        Users newUser = usersService.save(user);
        return ResponseEntity.ok(new Response("Success",true,user));

    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody UpdateProfileRequest u) {
        Users user = usersService.update(Utils.getUserId(), u);
        return ResponseEntity.ok(new Response("Success",true,user));

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findUserById(@PathVariable(name = "id") Long id) {
        Users user = usersService.findById(id);
        return ResponseEntity.ok(new Response("Success",true,user));

    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable(name = "id") Long id) {
        usersService.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?>  searchForUser(@RequestParam(required = true) String username, Pageable page) {
        Page<Users> users = usersService.searchUsers(username, page);
        return ResponseEntity.ok(new Response("Success",true,users));
    }

    @RequestMapping(value = "/loggedinUser", method = RequestMethod.GET)
    public ResponseEntity<?> loggedinUser() {

        UserPrinciple userDetails = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        //Users user = usersService.findById(Utils.getUserId());
        return ResponseEntity.ok(new Response("Success",true,userDetails));

    }


    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDTO u) {
        Users user = usersService.updatePassword(Utils.getUserId(), u);
        return ResponseEntity.ok(new Response("Success",true,user));

    }

}
