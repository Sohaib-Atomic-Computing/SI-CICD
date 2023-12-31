package io.satra.iconnect.service.user;

import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.repository.MerchantRepository;
import io.satra.iconnect.repository.UserRepository;
import io.satra.iconnect.repository.ValidatorRepository;
import io.satra.iconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ValidatorRepository validatorRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // check if the username exists in the user table, if not check in the validator table
        User user = null;
        Validator validator = null;
        Merchant merchant = null;

        if (userRepository.findFirstByEmailOrMobile(username, username).isPresent()) {
            user = userRepository.findFirstByEmailOrMobile(username, username).get();
            return UserPrincipal.build(user);
        } else if (validatorRepository.findFirstByName(username).isPresent()) {
            validator = validatorRepository.findFirstByName(username).get();
            return UserPrincipal.build(validator);
        } else if (merchantRepository.findFirstByAdminEmail(username).isPresent()) {
            merchant = merchantRepository.findFirstByAdminEmail(username).get();
            return UserPrincipal.build(merchant);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public Boolean checkToken(String username, String token) throws UsernameNotFoundException {
        if (userRepository.findByToken(token).isPresent()) {
            return true;
        } else if (validatorRepository.findByNameAndToken(username, token).isPresent()) {
            return true;
        } else if (merchantRepository.findFirstByAdminEmail(username).isPresent()) {
            return true;
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }
}
