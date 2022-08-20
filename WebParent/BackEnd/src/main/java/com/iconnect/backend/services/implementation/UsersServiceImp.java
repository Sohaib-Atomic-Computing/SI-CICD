package com.iconnect.backend.services.implementation;

import com.iconnect.backend.dtos.RegisterRequest;
import com.iconnect.backend.exception.BadRequestException;
import com.iconnect.backend.exception.RecordNotFoundException;
import com.iconnect.backend.model.Users;
import com.iconnect.backend.repository.UsersRepository;
import com.iconnect.backend.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsersServiceImp implements UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public Page<Users> findAll(Pageable pgbl) {
        return usersRepository.findAll(pgbl);
    }

    @Override
    public Users findByEmail(String email) {
        return null;
    }

    @Override
    public Users findById(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("No User record exist for given id", id));
        return user;
    }

    @Override
    public Users save(RegisterRequest registerRequest) throws RecordNotFoundException {

        Optional<Users> user = usersRepository.findByEmail(registerRequest.getEmail().toLowerCase());

        if (user.isPresent()) {
            throw new BadRequestException("Email Already Exist" );
        }

        user = usersRepository.findByUserUniqueName(registerRequest.getUsername().toLowerCase());

        if (user.isPresent()) {
            throw new BadRequestException("UserName Already Exist");
        }

        Users newUser = new Users();

        newUser.setEmail(registerRequest.getEmail().toLowerCase());
        newUser.setUsername(registerRequest.getUsername());
        newUser.setUserUniqueName(registerRequest.getUsername().toLowerCase());
       // newUser.setDob(registerRequest.getDob());
        newUser.setResetToken(UUID.randomUUID().toString());
        newUser.setPassword(encoder.encode(registerRequest.getPassword()));

        Users savedUser = usersRepository.save(newUser);



        return savedUser;
    }

    /*@Override
    public Users update(Long userId, RegisterRequest registerRequest) {
        Users updatedUser = findById(userId);
        if (registerRequest.getGender() != null) {
            updatedUser.setGender(registerRequest.getGender().toString());
        }
       /* if (registerRequest.getDob() != null) {
            updatedUser.setDob(registerRequest.getDob());
        }

        return usersRepository.save(updatedUser);
    }*/

    @Override
    public void delete(Long id) throws RecordNotFoundException {
        usersRepository.deleteById(id);
    }

    @Override
    public Page<Users> searchUsers(String username, Pageable pgbl) {
        return usersRepository.findByUsernameIgnoreCaseContains(username, pgbl);
    }
}
