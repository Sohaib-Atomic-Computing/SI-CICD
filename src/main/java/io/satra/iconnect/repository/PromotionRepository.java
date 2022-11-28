package io.satra.iconnect.repository;

import io.satra.iconnect.entity.Promotion;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {

    // find promotion by name
    Optional<Promotion> findByName(String name);

    // find promotion by vendor and name
    Optional<Promotion> findByVendorAndName(Vendor vendor, String name);

    // find promotion by vendor
    Set<Promotion> findByVendor(Vendor vendor);

    // find promotion by vendor and user
    Set<Promotion> findByVendorAndUsers(Vendor vendor, User user);
}
