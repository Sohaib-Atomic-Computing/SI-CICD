package com.iconnect.backend.repository;


import com.iconnect.backend.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    
   // Optional<Users> findByEmailAndIsActiveOrUsernameAndIsActive(String email , Boolean isActiveEmail, String username, Boolean isActiveUsername);

    Optional<Users> findByUsernameAndIsActive(String username,Boolean isActive);
    
    Optional<Users> findByEmailOrUsername(String email , String username);

    Optional<Users> findByEmail(String email);
    
    Optional<Users> findByUsername(String username);

    Optional<Users> findByUserUniqueName(String userUniqueName);
    
    Optional<Users> findByResetToken(String token);
    
    Page<Users> findByUsernameIgnoreCaseContains(String username, Pageable pageable );

   
}