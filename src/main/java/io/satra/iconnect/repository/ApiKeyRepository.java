package io.satra.iconnect.repository;

import io.satra.iconnect.entity.ApiKey;
import io.satra.iconnect.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {

    Optional<ApiKey> findByKeyAndIsActiveIsTrue(String apiKey);

    Optional<ApiKey> findByNameAndApplication(String name, Application application);

    boolean existsByNameAndIdNotAndIsActiveTrue(String name, String apiKeyId);

    List<ApiKey> findByApplicationAndIsActiveTrue(Application application);
}
