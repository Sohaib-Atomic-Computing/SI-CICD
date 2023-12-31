package io.satra.iconnect.repository;

import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValidatorRepository extends JpaRepository<Validator, String> {

    Optional<Validator> findByName(String name);
    Optional<Validator> findFirstByName(String name);
    Optional<Validator> findByValidatorKey(String validatorKey);
    Optional<Validator> findByVendorAndName(Vendor vendor, String name);
    List<Validator> findAllByVendor(Vendor vendor);
    Optional<Validator> findByNameAndToken(String name, String token);
}
