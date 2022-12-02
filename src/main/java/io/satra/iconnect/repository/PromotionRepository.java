package io.satra.iconnect.repository;

import io.satra.iconnect.entity.Promotion;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String>, JpaSpecificationExecutor<Promotion> {

    // find promotion by name
    Optional<Promotion> findByName(String name);

    // find promotion by vendor and name
    Optional<Promotion> findByVendorAndName(Vendor vendor, String name);

    // find promotion by vendor
    Set<Promotion> findByVendor(Vendor vendor);

    // find promotion by vendor and user
    Set<Promotion> findByVendorAndUsers(Vendor vendor, User user);

    //find all by name containing and status and start date greater than or equal to and end date less than or equal to
    Page<Promotion> findAllByNameContainingAndIsActiveAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            String name, Boolean status, LocalDateTime startDate, LocalDateTime endDate, Pageable page
    );
    //find all by name containing and start date greater than or equal to and end date less than or equal to
    Page<Promotion> findAllByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            String name, LocalDateTime startDate, LocalDateTime endDate, Pageable page
    );
    // find by name containing and status
    Page<Promotion> findAllByNameContainingAndIsActive(String name, Boolean isActive, Pageable page);
    // find by name containing
    Page<Promotion> findAllByNameContaining(String name, Pageable page);
    // find by status
    Page<Promotion> findAllByIsActive(Boolean isActive, Pageable page);
}
