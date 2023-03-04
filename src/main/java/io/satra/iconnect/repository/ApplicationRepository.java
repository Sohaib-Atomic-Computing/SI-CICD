package io.satra.iconnect.repository;

import io.satra.iconnect.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

    Optional<Application> findByName(String name);

    Optional<Application> findByNameAndIdNot(String name, String id);
}
