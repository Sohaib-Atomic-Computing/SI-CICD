package io.satra.iconnect.vendor_service.repository;

import io.satra.iconnect.vendor_service.entity.Validator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidatorRepository extends JpaRepository<Validator, String> {

}
