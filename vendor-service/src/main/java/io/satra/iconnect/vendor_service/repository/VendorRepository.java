package io.satra.iconnect.vendor_service.repository;

import io.satra.iconnect.vendor_service.entity.Validator;
import io.satra.iconnect.vendor_service.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {
    Optional<Vendor> findFirstByValidatorsContaining(Validator validator);
}
