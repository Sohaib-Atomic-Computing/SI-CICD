package io.satra.iconnect.user_service.repository;

import io.satra.iconnect.user_service.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

  Optional<User> findByEmailAndIsActive(String email, Boolean isActive);

  Optional<User> findByPhoneNumberAndIsActive(String phoneNumber, Boolean isActive);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(String phoneNumber);

  Page<User> findByPhoneNumberIgnoreCaseContains(String phoneNumber, Pageable pageable);
}
