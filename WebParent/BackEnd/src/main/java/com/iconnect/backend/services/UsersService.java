package com.iconnect.backend.services;


import com.iconnect.backend.dtos.RegisterRequest;
import com.iconnect.backend.dtos.UpdateProfileRequest;
import com.iconnect.backend.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {

    Page<Users> findAll(Pageable pgbl);

    Users findByEmail(String email);

    Users findById(Long id);

    Users save(RegisterRequest u);

    Users update(Long userId, UpdateProfileRequest updateProfileRequest);

    void delete(Long id);
    
    Page<Users> searchUsers(String phoneNumber , Pageable pgbl);

    Boolean generateOTP(String phoneNumber);
}
