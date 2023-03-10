package io.satra.iconnect.repository;

import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByIdAndIsActive(String id, Boolean isActive);
    // find user by email or mobile number
    Optional<User> findFirstByEmailOrMobile(String email, String mobile);
    // find user by email
    Optional<User> findByEmail(String email);
    //find user by mobile number
    Optional<User> findByMobile(String mobile);
    // find user by role
    List<User> findByRole(UserRole role);
    // find user by token
    Optional<User> findByToken(String token);
}
