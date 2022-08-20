package com.iconnect.backend.repository;

import com.iconnect.backend.model.Tokens;
import com.iconnect.backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author kalsumaykhi 02/08/2020
 */
@Repository
public interface TokensRepository extends JpaRepository<Tokens,Long> {

  Tokens findByUser(Users user);

}
