package io.satra.iconnect.repository;

import io.satra.iconnect.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {

    // find vendor by name
    Optional<Vendor> findByName(String name);
}
