package io.satra.iconnect.user_service.repository;

import io.satra.iconnect.user_service.entity.UserRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, String> {

  Optional<UserRefreshToken> findByToken(String token);
}
