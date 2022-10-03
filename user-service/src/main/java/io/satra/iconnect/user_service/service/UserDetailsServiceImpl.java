package io.satra.iconnect.user_service.service;


import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.repository.UserRepository;
import io.satra.iconnect.user_service.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userRepository.findByEmailOrPhoneNumber(username, username).orElseThrow(() ->
        new UsernameNotFoundException("No user for phoneNumber or email %s found!".formatted(username))
    );

    return UserPrincipal.builder().user(user).build();
  }
}
