package io.satra.iconnect.user_service.repository;

import io.satra.iconnect.user_service.entity.DeviceToken;
import io.satra.iconnect.user_service.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {

  Optional<DeviceToken> findByUser(User user);
}
