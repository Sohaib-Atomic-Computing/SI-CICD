package io.satra.iconnect.repository;

import io.satra.iconnect.entity.ApiKey;
import io.satra.iconnect.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {

    Optional<ApiKey> findByKeyAndStatusIsTrue(String apiKey);
    Optional<ApiKey> findByNameAndApplication(String name, Application application);

    Optional<Object> findByNameAndIdNot(String name, String apiKeyId);

    List<ApiKey> findByApplication(Application application);
}
