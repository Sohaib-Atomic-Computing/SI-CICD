package com.iconnect.backend.services.implementation;


import com.iconnect.backend.Utils.Utils;
import com.iconnect.backend.exception.RecordNotFoundException;
import com.iconnect.backend.model.Tokens;
import com.iconnect.backend.model.Users;
import com.iconnect.backend.repository.TokensRepository;
import com.iconnect.backend.services.TokenService;
import com.iconnect.backend.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kalsumaykhi 02/08/2020
 */

@Service
public class TokenServiceImpl implements TokenService {

  @Autowired
  private TokensRepository tokensRepository;

  @Autowired
  private UsersService usersService;
  @Override
  public List<Tokens> findAll() {
    return tokensRepository.findAll();
  }

  @Override
  public Tokens findById(Long Id) {
    return tokensRepository.findById(Id).orElseThrow(() -> new RecordNotFoundException("Token not found"));
  }

  @Override
  public Tokens add(String token) {
    Users user = usersService.findById(Utils.getUserId());
    Tokens deviceToken = new Tokens();
    deviceToken.setToken(token);
    deviceToken.setUser(user);
    return tokensRepository.save(deviceToken);
  }

  @Override
  public Tokens update(String token) {
    Users user = usersService.findById(Utils.getUserId());
    Tokens deviceToken = tokensRepository.findByUser(user);
    deviceToken.setToken(token);
    return tokensRepository.save(deviceToken);
  }
}
