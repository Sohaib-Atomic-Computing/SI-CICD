package io.satra.iconnect.utils;

import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.security.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JWTToken {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    /**
     * Generate a JWT token for the user
     *
     * @param emailOrMobile the user email or mobile number
     * @param password the user password
     * @return the generated JWT token
     */
    public String generate(String emailOrMobile, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrMobile, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }
}
