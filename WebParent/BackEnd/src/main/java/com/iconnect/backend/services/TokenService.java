package com.iconnect.backend.services;

import com.iconnect.backend.model.Tokens;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kalsumaykhi 02/08/2020
 */
@Service
public interface TokenService {

  List<Tokens> findAll();

  Tokens findById(Long Id);

  Tokens add(String token);

  Tokens update(String token);


}
