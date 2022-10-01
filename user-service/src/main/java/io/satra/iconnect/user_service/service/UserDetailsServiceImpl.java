package io.satra.iconnect.user_service.service;


import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.repository.UserRepository;
import io.satra.iconnect.user_service.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

      User user = userRepository.findByEmailOrPhoneNumber(username, username)
          .orElseThrow(() ->
              new UsernameNotFoundException("PhoneNumber or Email Not Found with : " + username)
          );

      return UserPrincipal.build(user);
    }
}
