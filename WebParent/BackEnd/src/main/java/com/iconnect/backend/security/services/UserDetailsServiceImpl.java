package com.iconnect.backend.security.services;


import com.iconnect.backend.model.Users;
import com.iconnect.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsersRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
    	
        Users user = userRepository.findByEmailOrPhoneNumber(username , username)
                	.orElseThrow(() -> 
                        new UsernameNotFoundException("PhoneNumber or Email Not Found with : " + username)
        );

        return UserPrinciple.build(user);
    }
}