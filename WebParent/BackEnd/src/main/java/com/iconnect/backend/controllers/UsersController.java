package com.iconnect.backend.controllers;


import com.iconnect.backend.Utils.Utils;
import com.iconnect.backend.dtos.RegisterRequest;
import com.iconnect.backend.model.Users;
import com.iconnect.backend.services.UsersService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Users> saveNewUser(@RequestBody RegisterRequest user) {
        Users newUser = usersService.save(user);
        return ResponseEntity.ok(newUser);

    }

    /*@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody RegisterRequest u) {
        Users user = usersService.update(id, u);
        return ResponseEntity.ok(user);

    }*/

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Users> findUserById(@PathVariable(name = "id") Long id) {
        Users user = usersService.findById(id);
        return ResponseEntity.ok(user);

    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable(name = "id") Long id) {
        usersService.delete(id);
    }

    @GetMapping("/search")
    public Page<Users> searchForUser(@RequestParam(required = true) String username, Pageable page) {
        Page<Users> users = usersService.searchUsers(username, page);
        return users;
    }

    @RequestMapping(value = "/loggedinUser", method = RequestMethod.GET)
    public ResponseEntity<Users> loggedinUser() {
        Users user = usersService.findById(Utils.getUserId());
        return ResponseEntity.ok(user);

    }

}
