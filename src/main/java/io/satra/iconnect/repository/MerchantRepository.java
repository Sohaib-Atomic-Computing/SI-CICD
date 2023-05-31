package io.satra.iconnect.repository;

import io.satra.iconnect.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String> {
    Optional<Merchant> findFirstByAdminEmail(String emailAddress);
    Optional<Merchant> findFirstByAdminEmailOrMobile(String emailAddress, String mobileNumber);
}
